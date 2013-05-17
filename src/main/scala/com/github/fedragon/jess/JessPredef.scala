package com.github.fedragon.jess

/**
 * Predefined classes and type aliases
 */
object JessPredef {

	type Validator = (String, RichJsValue)

	// Play JSon type aliases
	def parse(jsonString: String): JsValue = play.api.libs.json.Json.parse(jsonString)

  type JsValue  = play.api.libs.json.JsValue
  type JsString = play.api.libs.json.JsString
  type JsNumber = play.api.libs.json.JsNumber
  type JsObject = play.api.libs.json.JsObject
  type JsArray  = play.api.libs.json.JsArray
}

object JessShortcuts {

	import JessPredef._

	def using(js: JsValue)(rich: RichJsObject) = {
		rich(js)
	}

	def json(f: Validator) = RichJsObject(Seq(f))

	def json(f: Validator, g: Validator) = RichJsObject(Seq(f, g))
}

object JessImplicits {
	import JessPredef._

	class JsFieldName(name: String) {
		def asNum(f: JsNumber => Boolean) = (name, new RichJsNumber(f))
		def asObj(seq: Seq[Validator]) = (name, new RichJsObject(seq))
	}

	implicit def stringToJsFieldName(str: String) = new JsFieldName(str)
}