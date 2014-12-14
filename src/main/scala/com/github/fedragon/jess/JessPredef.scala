package com.github.fedragon.jess

import scala.util.{Try, Success, Failure}

import scalaz._, scalaz.Scalaz._

/**
 * Jess common classes, utility methods and type aliases
 */
object JessPredef {

  class Validator(val field: Symbol, val rule: JsValueRule)

  implicit def pairToValidator(pair: (Symbol, JsValueRule)) = new Validator(pair._1, pair._2)

  /**
   * Applies the rule to the js value and returns its result.
   *
   * @param js json value to validate
   * @param rule rule to use to validate the json value
   * @return validation result
   */
	def verifyThat (js: JsValue)(rule: JsObjectRule): ValidationNel[JsValue, Unit] = rule(js)

  /**
   * Applies the rule to the js document and returns the validation result.
   *
   * @param jsonString json value to validate
   * @param rule rule to use to validate the json value
   * @return validation result
   *
   */
  def verifyThat (jsonString: String)(rule: JsObjectRule): ValidationNel[JsValue, Unit]  = {
    Try(parse(jsonString)) match {
      case Success(parsedJson) => rule(parsedJson)
      case Failure(_) => new JsUndefined("Invalid json!").failureNel[Unit]
    }
  }

  /**
   * Creates and returns an object rule combining the provided validators.
   *
   * @param f pair (field_name, rule)
   * @param g additional (optional) validators
   * @return created object rule
   */
	def obj (f: Validator, g: Validator*): JsObjectRule = JsObjectRule(f +: g)

  /**
   * Creates and returns an array rule combining the provided validators.
   *
   * @param values list of values to check
   * @return created array rule
   */
  def array (values: Any*) = {
	  JsArrayRule(
      values map {
        case b: Boolean => JsBooleanRule(b)
        case s: String => JsStringRule(t => t == s)
        case jr: JsValueRule => jr
        case BigDecimalExtractor(bd) => JsNumberRule(n => n == bd)
        case other => throw new IllegalArgumentException(s"Invalid value: $other")
      }
	  )
  }

	// Play JSon type aliases
	def parse (jsonString: String): JsValue = play.api.libs.json.Json.parse(jsonString)

  type JsValue  = play.api.libs.json.JsValue
  type JsBoolean = play.api.libs.json.JsBoolean
  type JsString = play.api.libs.json.JsString
  type JsNumber = play.api.libs.json.JsNumber
  type JsObject = play.api.libs.json.JsObject
  type JsArray  = play.api.libs.json.JsArray
  type JsUndefined  = play.api.libs.json.JsUndefined

  val JsNull = play.api.libs.json.JsNull
  // ---

  private object BigDecimalExtractor {
    def unapply(v: Any): Option[BigDecimal] = {
      v match {
        case bd: BigDecimal => Some(bd)
        case s: Short => Some(BigDecimal(s))
        case i: Int => Some(BigDecimal(i))
        case l: Long => Some(BigDecimal(l))
        case d: Double => Some(BigDecimal(d))
        case f: Float => Some(BigDecimal(f))
        case other => None
      }
    }
  }
}
