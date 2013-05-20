package com.github.fedragon.jess

import scala.util.{Try, Success, Failure}

/**
 * Predefined classes and type aliases
 */
object JessPredef {

	type Validator = (String, JsValueRule)

	def using(js: JsValue)(rule: JsObjectRule) = rule(js)

  def using(jsonString: String)(rule: JsObjectRule) = {
    Try(parse(jsonString)) match {
      case Success(parsedJson) => rule(parsedJson)
      case Failure(_) => throw new IllegalArgumentException("Invalid json!")
    }
  }

	def obj(f: Validator, g: Validator*) = JsObjectRule(Seq(f) ++ g)

  def array(values: Any*) = {
    val rules =
      values map { expected =>
        expected match {
          case b: Boolean => JsBooleanRule(b)
          case s: String => JsStringRule(t => t == s)
          case jr: JsValueRule => jr
          case BigDecimalExtractor(bd) => JsNumberRule(n => n == bd)
        }
      }

    JsArrayRule(rules)
  }

  private object BigDecimalExtractor {
    def unapply(v: Any): Option[BigDecimal] = {
      val result =
        v match {
          case bd: BigDecimal => bd
          case s: Short => BigDecimal(s)
          case i: Int => BigDecimal(i)
          case l: Long => BigDecimal(l)
          case d: Double => BigDecimal(d)
          case f: Float => BigDecimal(f)
          case other => throw new IllegalArgumentException(s"Invalid value: $other")
        }
      Some(result)
    }
  }

  case class Result(passed: Seq[JsValue], failed: Seq[JsValue]) {

    val allesInOrde = !passed.isEmpty && failed.isEmpty
  }

	// Play JSon type aliases
	def parse(jsonString: String): JsValue = play.api.libs.json.Json.parse(jsonString)

  type JsValue  = play.api.libs.json.JsValue
  type JsBoolean = play.api.libs.json.JsBoolean
  type JsString = play.api.libs.json.JsString
  type JsNumber = play.api.libs.json.JsNumber
  type JsObject = play.api.libs.json.JsObject
  type JsArray  = play.api.libs.json.JsArray
  type JsUndefined  = play.api.libs.json.JsUndefined
}