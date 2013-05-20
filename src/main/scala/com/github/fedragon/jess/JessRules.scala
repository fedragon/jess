package com.github.fedragon.jess

import JessPredef._

abstract sealed class JsValueRule {
	def apply(js: JsValue): Boolean

	def invalidInput(input: JsValue) = throw new IllegalArgumentException(s"Invalid input: $input")
}

case class JsBooleanRule(f: Boolean) extends JsValueRule {
	def apply(js: JsValue): Boolean = {
		js match {
			case bool: JsBoolean => f == bool.value
			case other => invalidInput(other)
		}
	}
}

case class JsNumberRule(f: BigDecimal => Boolean) extends JsValueRule {
	def apply(js: JsValue): Boolean = {
		js match {
			case num: JsNumber => f(num.value)
			case other => invalidInput(other)
		}
	}
}

case class JsStringRule(f: String => Boolean) extends JsValueRule {
	def apply(js: JsValue): Boolean = {
		js match {
			case str: JsString => f(str.value)
			case other => invalidInput(other)
		}
	}	
}

case class JsObjectRule(validators: Seq[Validator]) extends JsValueRule {

	def apply(js: JsValue): Boolean = {
		js match {
			case obj: JsObject =>
				validators forall { validator =>
					obj \ validator._1 match {
						case notFound: JsUndefined => 
							false
						case value @ _ =>
							validator._2(value)
					}
				}
			case other => invalidInput(other)
		}
	}
}

case class JsArrayRule(rules: Seq[JsValueRule]) extends JsValueRule {

	def apply(js: JsValue): Boolean = {
		val results =
			js match {
				case array: JsArray =>
					rules map { rule =>
						val fields = filterFieldsByRule(array.value, rule)
						fields exists { field => rule(field) }
					}
				case other => invalidInput(other)
			}

		results forall ( r => r == true )
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