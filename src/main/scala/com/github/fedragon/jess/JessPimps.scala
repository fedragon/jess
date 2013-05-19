package com.github.fedragon.jess

import JessPredef._

class PimpedJsField(name: String) {

	def is(expected: BigDecimal) = (name, new JsNumberRule(n => n == expected))
	def is(expected: String) = (name, new JsStringRule(s => s == expected))
	def is(seq: Validator*) = (name, new JsObjectRule(seq))

	def asNum(f: BigDecimal => Boolean) = (name, new JsNumberRule(f))
	def asStr(f: String => Boolean) = (name, new JsStringRule(f))
	def asObj(seq: Validator*) = (name, new JsObjectRule(seq))
}

object PimpedJsField {
	implicit def stringToPimpedJsField(fieldName: String) = new PimpedJsField(fieldName)
}
