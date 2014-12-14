package com.github.fedragon.jess

import JessPredef._

abstract class JsValueRule {
	def apply(js: JsValue): Result[JsValue]

	private[jess] def invalidInput(input: JsValue) = throw new IllegalArgumentException(s"Invalid input: $input")
}

case class JsBooleanRule(b: Boolean) extends JsValueRule {
	def apply(js: JsValue): Result[JsValue] = {
		js match {
			case bool: JsBoolean => 
				if (b == bool.value) Ok
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

trait Reduce {
	def reduce (results: Seq[Result[JsValue]]): Result[JsValue] = {
		results reduceLeft { (a, b) =>
			(a, b) match {
				case (nok @ Nok(_), Ok) => nok
				case (Ok, nok @ Nok(_)) => nok
				case (Ok, Ok) => a
				case (nok1 @ Nok(_), nok2 @ Nok(_)) => Nok(nok1.failed ++ nok2.failed)
			}
		}
	}
}

case class JsObjectRule(validators: Seq[Validator]) extends JsValueRule with Reduce {

	def apply(js: JsValue): Result[JsValue] = {
		js match {
			case obj: JsObject =>
				reduce (
					validators map { validator =>
						obj \ validator.field.name match {
							case _: JsUndefined => 
								Nok(Seq(new JsUndefined(validator.field.name)))
							case value @ _ =>
								validator.rule(value)
						}
					}
				)
			case other => invalidInput(other)
		}
	}
}

case class JsArrayRule(rules: Seq[JsValueRule]) extends JsValueRule with Reduce {

	def apply(js: JsValue): Result[JsValue] = {
		val results =
			js match {
				case array: JsArray => rules map (rule => getRuleResult(array, rule))
				case other => invalidInput(other)
			}

		reduce (results)
	}

	private def getRuleResult(array: JsArray, rule: JsValueRule): Result[JsValue] = {
		val applicableFields = filterFieldsByRule(array.value, rule)

		val validField: Option[Result[JsValue]] =
			applicableFields.map(rule(_)).find {
				case Ok => true
				case _ => false
			}

		validField.getOrElse(Nok(Seq(array)))
	}

	private def filterFieldsByRule(fields: Seq[JsValue], rule: JsValueRule): Seq[JsValue] = {
		rule match {
			case _: JsArrayRule   => fields.collect { case jsa: JsArray => jsa }
			case _: JsBooleanRule => fields.collect { case jsb: JsBoolean => jsb }
			case _: JsNumberRule  => fields.collect { case jsn: JsNumber => jsn }
			case _: JsObjectRule  => fields.collect { case jso: JsObject => jso }
			case _: JsStringRule  => fields.collect { case jss: JsString => jss }
			case other => throw new IllegalArgumentException(s"Unsupported rule: $other")
		}
	}
}
