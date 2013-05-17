package com.github.fedragon.jess

/**
 * Predefined classes and type aliases
 */
object JessPredef {

	type Validator = (String, JsValue => Boolean)

	def json(fs: Validator*) = RichJsObject(fs)
  
	// Play JSon type aliases
	def parse(jsonString: String): JsValue = play.api.libs.json.Json.parse(jsonString)

  type JsValue  = play.api.libs.json.JsValue
  type JsString = play.api.libs.json.JsString
  type JsNumber = play.api.libs.json.JsNumber
  type JsObject = play.api.libs.json.JsObject
  type JsArray  = play.api.libs.json.JsArray
}

import JessPredef._

case class RichJsObject(fields: Seq[Validator]) {

	def apply(js: JsObject) {

		fields.forall { field =>


		}
	}
}

/*
object JessImplicits {
	implicit def toRichJsObject (input: JsObject) = new RichJsObject(input)
}
*/