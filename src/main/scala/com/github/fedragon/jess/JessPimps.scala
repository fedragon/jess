package com.github.fedragon.jess

import JessPredef._
import scala.util.matching.Regex

class PimpedJsField(name: String) {

	def is(expected: BigDecimal) = (name, JsNumberRule(n => n == expected))
	def is(expected: String) = (name, JsStringRule(s => s == expected))
	def is(seq: Validator*) = (name, JsObjectRule(seq))

	def contains(seq: JsValueRule*) = (name, JsArrayRule(seq))

	def asNum(f: BigDecimal => Boolean) = (name, JsNumberRule(f))
	def asStr(f: String => Boolean) = (name, JsStringRule(f))
	def asObj(seq: Validator*) = (name, JsObjectRule(seq))
}

object ImplicitPimps {
	implicit def stringToPimpedJsField(fieldName: String) = new PimpedJsField(fieldName)
}
