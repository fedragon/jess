package com.github.fedragon.jess

/**
 * Predefined classes and type aliases
 */
object JessPredef {

	type Validator = (String, JsValueRule)

	def using(js: JsValue)(rule: JsObjectRule) = rule(js)

  def using(js: String)(rule: JsObjectRule) = rule(parse(js))

	def json(f: Validator, g: Validator*) = JsObjectRule(Seq(f) ++ g)

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