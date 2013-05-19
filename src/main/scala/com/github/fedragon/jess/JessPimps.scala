package com.github.fedragon.jess

import JessPredef._

class PimpedJsField(name: String) {

	def is(expected: Number) = {
		(name, new JsNumberRule(js => js.value == expected))
	}

	def is(expected: String) = {
		(name, new JsStringRule(js => js.value == expected))
	}

	def is(seq: Validator*) = (name, new JsObjectRule(seq))

	def asNum(f: JsNumber => Boolean) = (name, new JsNumberRule(f))
	def asStr(f: JsString => Boolean) = (name, new JsStringRule(f))
	def asObj(seq: Validator*) = (name, new JsObjectRule(seq))
}

object PimpedJsField {
	implicit def stringToPimpedJsField(str: String) = new PimpedJsField(str)
}
