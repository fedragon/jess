package com.github.fedragon.jess

/**
 * Predefined classes and type aliases
 */
object JessPredef {

  /**
   * Validation result
   */
  abstract sealed class ValidationResult
  /**
   * Valid result
   */
  case class Geldig(path: JessPath) extends ValidationResult
  /**
   * Invalid result
   */
  case class Ongeldig(errors: Errors) extends ValidationResult
  
  // Internal type aliases
	type Errors = Map[JessPath, Seq[String]]

	// Play JSon type aliases
	def parse(jsonString: String): JsValue = play.api.libs.json.Json.parse(jsonString)

  type JsValue  = play.api.libs.json.JsValue
  type JsNumber = play.api.libs.json.JsNumber
  type JsObject = play.api.libs.json.JsObject
  type JsArray  = play.api.libs.json.JsArray
}