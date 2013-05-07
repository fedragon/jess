package com.github.fedragon.jess

/**
 * Predefined classes and type aliases
 */
object JessPredef {

  /**
   * Validation result
   */
  abstract sealed class ValidationResult(path: JessPath)
  /**
   * Valid result
   */
  case class Geldig(path: JessPath) extends ValidationResult(path)
  /**
   * Invalid result
   */
  case class Ongeldig(path: JessPath, issues: Seq[String]) extends ValidationResult(path)
  
	// Play JSon type aliases
	def parse(jsonString: String): JsValue = play.api.libs.json.Json.parse(jsonString)

  type JsValue  = play.api.libs.json.JsValue
  type JsString = play.api.libs.json.JsString
  type JsNumber = play.api.libs.json.JsNumber
  type JsObject = play.api.libs.json.JsObject
  type JsArray  = play.api.libs.json.JsArray
}