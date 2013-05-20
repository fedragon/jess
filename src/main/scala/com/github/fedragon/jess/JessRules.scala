package com.github.fedragon.jess

import JessPredef._

abstract sealed class JsValueRule {
	def apply(js: JsValue): Result

	def invalidInput(input: JsValue) = throw new IllegalArgumentException(s"Invalid input: $input")
}

case class JsBooleanRule(f: Boolean) extends JsValueRule {
	def apply(js: JsValue): Result = {
		js match {
			case bool: JsBoolean => 
				if(f == bool.value) 
					Result(Seq(bool), Seq.empty)
				else Result(Seq.empty, Seq(bool))
			case other => invalidInput(other)
		}
	}
}

case class JsNumberRule(f: BigDecimal => Boolean) extends JsValueRule {
	def apply(js: JsValue): Result = {
		js match {
			case num: JsNumber => 
				if(f(num.value))
					Result(Seq(num), Seq.empty)
				else Result(Seq.empty, Seq(num))
			case other => invalidInput(other)
		}
	}
}

case class JsStringRule(f: String => Boolean) extends JsValueRule {
	def apply(js: JsValue): Result = {
		js match {
			case str: JsString => 
				if(f(str.value))
					Result(Seq(str), Seq.empty)
				else Result(Seq.empty, Seq(str))
			case other => invalidInput(other)
		}
	}	
}

trait ComplexRule {
	def reduce(results: Seq[Result]): Result = {
		def reduce(res: Seq[Result], passed: Seq[JsValue], failed: Seq[JsValue]): Result = {
			if(res.isEmpty) Result(passed, failed)
			else {
				val next = res.head

				if(next.failed.isEmpty) 
					reduce(res.tail, passed ++ next.passed, failed)
				else reduce(res.tail, passed, failed ++ next.failed)
			}
		}

		reduce(results, Seq.empty, Seq.empty)
	}
}

case class JsObjectRule(validators: Seq[Validator]) extends JsValueRule with ComplexRule {

	def apply(js: JsValue): Result = {
		js match {
			case obj: JsObject =>
				val results = 
					validators map { validator =>
						obj \ validator._1 match {
							//case notFound: JsUndefined => 
							//	false
							case value @ _ =>
								validator._2(value)
						}
					}

				reduce(results)
			case other => invalidInput(other)
		}
	}
}

case class JsArrayRule(rules: Seq[JsValueRule]) extends JsValueRule with ComplexRule {

	def apply(js: JsValue): Result = {
		val results =
			js match {
				case array: JsArray =>
					rules map { rule =>
						getRuleResult(array, rule)
					}
				case other => invalidInput(other)
			}

		reduce(results)
	}

	private def getRuleResult(array: JsArray, rule: JsValueRule): Result = {
		val applicableFields = filterFieldsByRule(array.value, rule)

		val validField: Option[Result] =
			applicableFields.map(field => rule(field))
				.find { res => 
					res match {
						case Result(passed, Seq()) => true
						case _ => false
					}
				}

		validField match {
			case Some(result) => result
			case None => Result(Seq.empty, Seq(array))
		}
	}

	private def filterFieldsByRule(fields: Seq[JsValue], rule: JsValueRule): Seq[JsValue] = {
		rule match {
			case a: JsArrayRule => fields.filter(f => f.isInstanceOf[JsArray])
			case b: JsBooleanRule => fields.filter(f => f.isInstanceOf[JsBoolean])
			case n: JsNumberRule => fields.filter(f => f.isInstanceOf[JsNumber])
			case o: JsObjectRule => fields.filter(f => f.isInstanceOf[JsObject])
			case s: JsStringRule => fields.filter(f => f.isInstanceOf[JsString])
			case other => throw new IllegalArgumentException(s"Unsupported rule: $other")
		}
	}
}