package com.github.fedragon.jess

/**
 * Predefined classes and type aliases
 */
object JessPredef {

	type Validator = (String, JsValueRule)

	def using(js: JsValue)(rule: JsObjectRule) = rule(js)

	def json(f: Validator, g: Validator*) = JsObjectRule(Seq(f) ++ g)

	// Play JSon type aliases
	def parse(jsonString: String): JsValue = play.api.libs.json.Json.parse(jsonString)

  type JsValue  = play.api.libs.json.JsValue
  type JsString = play.api.libs.json.JsString
  type JsNumber = play.api.libs.json.JsNumber
  type JsObject = play.api.libs.json.JsObject
  type JsArray  = play.api.libs.json.JsArray
  type JsUndefined  = play.api.libs.json.JsUndefined
}

object JessImplicits {
	import JessPredef._

	class PimpedJsField(name: String) {
		
		def is(expected: Number) = {
			val f = (js: JsNumber) => js.value == expected
			(name, new JsNumberRule(f))
		}

		def is(expected: String) = {
			val f = (js: JsString) => js.value == expected
			(name, new JsStringRule(f))
		}

		def asNum(f: JsNumber => Boolean) = (name, new JsNumberRule(f))
		def asObj(seq: Seq[Validator]) = (name, new JsObjectRule(seq))
		def asStr(f: JsString => Boolean) = (name, new JsStringRule(f))
	}

	implicit def stringToPimpedJsField(str: String) = new PimpedJsField(str)
}