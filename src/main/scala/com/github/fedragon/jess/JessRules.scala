package com.github.fedragon.jess

import JessPredef._
import scalaz._, scalaz.Scalaz._

abstract class JsValueRule {
  val succeeded = ().successNel[JsValue]

	def apply(js: JsValue): ValidationNel[JsValue, Unit]

	private[jess] def invalidInput(input: JsValue): ValidationNel[JsValue, Unit] = input.failureNel[Unit]
}

case class JsBooleanRule(b: Boolean) extends JsValueRule {
	def apply(js: JsValue): ValidationNel[JsValue, Unit] = {
		js match {
			case bool: JsBoolean =>
				if (b == bool.value) succeeded
        else bool.failureNel[Unit]
			case other => invalidInput(other)
		}
	}
}

case class JsNumberRule(f: BigDecimal => Boolean) extends JsValueRule {
	def apply(js: JsValue): ValidationNel[JsValue, Unit] = {
		js match {
			case num: JsNumber =>
				if (f(num.value)) succeeded
				else num.failureNel[Unit]
			case other => invalidInput(other)
		}
	}
}

case class JsStringRule(f: String => Boolean) extends JsValueRule {
	def apply(js: JsValue): ValidationNel[JsValue, Unit] = {
		js match {
			case str: JsString =>
				if (f(str.value)) succeeded
				else str.failureNel[Unit]
			case other => invalidInput(other)
		}
	}
}

trait Reduce {
	def reduce (results: List[ValidationNel[JsValue, Unit]]): ValidationNel[JsValue, Unit] =
    results.sequenceU.map(_.head)
}

case class JsObjectRule(validators: Seq[Validator]) extends JsValueRule with Reduce {

	def apply(js: JsValue): ValidationNel[JsValue, Unit] = {
		js match {
			case obj: JsObject =>
				reduce (
					validators.toList.map { validator =>
						obj \ validator.field.name match {
							case _: JsUndefined => new JsUndefined(validator.field.name).failureNel[Unit]
							case value @ _ => validator.rule(value)
						}
					}
				)
			case other => invalidInput(other)
		}
	}
}

case class JsArrayRule(rules: Seq[JsValueRule]) extends JsValueRule with Reduce {

	def apply(js: JsValue): ValidationNel[JsValue, Unit] = {
		val results =
			js match {
				case array: JsArray => rules map (rule => getRuleResult(array, rule)) toList
				case other => List(invalidInput(other))
			}

		reduce(results)
	}

	private def getRuleResult(array: JsArray, rule: JsValueRule): ValidationNel[JsValue, Unit] = {
		val applicableFields = filterFieldsByRule(array.value, rule)

		val validField: Option[ValidationNel[JsValue, Unit]] =
			applicableFields.map(rule(_)).find {
				case Success(_) => true
				case _ => false
			}

      validField.getOrElse(array.failureNel[Unit])
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
