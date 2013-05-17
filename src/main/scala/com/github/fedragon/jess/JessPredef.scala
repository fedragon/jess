package com.github.fedragon.jess

/**
 * Predefined classes and type aliases
 */
object JessPredef {

	type Validator = (String, RichJsValue)

	//def json(fs: Validator*) = RichJsObject(fs)

	def json(f: Validator) = RichJsObject(Seq(f))

	def json(f: Validator, g: Validator) = RichJsObject(Seq(f, g))
  
	// Play JSon type aliases
	def parse(jsonString: String): JsValue = play.api.libs.json.Json.parse(jsonString)

  type JsValue  = play.api.libs.json.JsValue
  type JsString = play.api.libs.json.JsString
  type JsNumber = play.api.libs.json.JsNumber
  type JsObject = play.api.libs.json.JsObject
  type JsArray  = play.api.libs.json.JsArray
}

/*
object JessImplicits {
	implicit def toRichJsObject (input: JsObject) = new RichJsObject(input)
}
*/
