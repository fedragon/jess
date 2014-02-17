package com.github.fedragon.jess

import JessPredef._

abstract class JsValueRule {
	def apply(js: JsValue): Result[JsValue]

	private[jess] def invalidInput(input: JsValue) = throw new IllegalArgumentException(s"Invalid input: $input")
}

case class JsBooleanRule(f: Boolean) extends JsValueRule {
	def apply(js: JsValue): Result[JsValue] = {
		js match {
			case bool: JsBoolean => 
				if (f == bool.value) Ok
				else Nok(Seq(bool))
			case other => invalidInput(other)
		}
	}
}

case class JsNumberRule(f: BigDecimal => Boolean) extends JsValueRule {
	def apply(js: JsValue): Result[JsValue] = {
		js match {
			case num: JsNumber => 
				if (f(num.value)) Ok
				else Nok(Seq(num))
			case other => invalidInput(other)
		}
	}
}

case class JsStringRule(f: String => Boolean) extends JsValueRule {
	def apply(js: JsValue): Result[JsValue] = {
		js match {
			case str: JsString => 
				if (f(str.value)) Ok
				else Nok(Seq(str))
			case other => invalidInput(other)
		}
	}	
}

trait ComplexRule {
	def compact (results: Seq[Result[JsValue]]): Result[JsValue] = {
		def compact (res: Seq[Result[JsValue]], failed: Seq[JsValue]): Result[JsValue] = {
			if(res.isEmpty) {
				failed match {
					case Seq() => Ok
					case fields => Nok(fields)
				}
			} else {
				res.head match {
					case Ok => compact (res.tail, failed)
					case Nok(fields) => compact (res.tail, failed ++ fields)
				}
			}
		}

		compact (results, Seq.empty)
	}
}

case class JsObjectRule(validators: Seq[Validator]) extends JsValueRule with ComplexRule {

	def apply(js: JsValue): Result[JsValue] = {
		js match {
			case obj: JsObject =>
				val results = 
					validators map { validator =>
						obj \ validator._1.name match {
							case _: JsUndefined => 
								Nok(Seq(new JsUndefined(validator._1.name)))
							case value @ _ =>
								validator._2(value)
						}
					}

				compact (results)
			case other => invalidInput(other)
		}
	}
}

case class JsArrayRule(rules: Seq[JsValueRule]) extends JsValueRule with ComplexRule {

	def apply(js: JsValue): Result[JsValue] = {
		val results =
			js match {
				case array: JsArray =>
					rules map { rule =>
						getRuleResult(array, rule)
					}
				case other => invalidInput(other)
			}

		compact (results)
	}

	private def getRuleResult(array: JsArray, rule: JsValueRule): Result[JsValue] = {
		val applicableFields = filterFieldsByRule(array.value, rule)

		val validField: Option[Result[JsValue]] =
			applicableFields.map(field => rule(field))
				.find { res => 
					res match {
						case Ok => true
						case _ => false
					}
				}

		validField match {
			case Some(result) => result
			case None => Nok(Seq(array))
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