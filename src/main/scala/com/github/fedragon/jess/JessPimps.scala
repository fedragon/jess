package com.github.fedragon.jess

import JessPredef._

trait AsBoolean {
	this: PimpedJsField =>
		def is(expected: Boolean) = (name, JsBooleanRule(expected))
		def asBool(f: Boolean) = (name, JsBooleanRule(f))
}

trait AsNumber {
	this: PimpedJsField =>
		def is(expected: BigDecimal) = (name, JsNumberRule(n => n == expected))
		def asNum(f: BigDecimal => Boolean) = (name, JsNumberRule(f))
}

trait AsString {
	this: PimpedJsField =>
		def is(expected: String) = (name, JsStringRule(s => s == expected))
		def asStr(f: String => Boolean) = (name, JsStringRule(f))
}

trait AsArray {
	this: PimpedJsField =>
		def is(rule: JsArrayRule) = (name, rule)
	  def asArray(seq: JsValueRule*) = (name, JsArrayRule(seq))
}

trait AsObject {
	this: PimpedJsField =>
		def is(seq: Validator*) = (name, JsObjectRule(seq))
		def asObj(seq: Validator*) = (name, JsObjectRule(seq))
}

class PimpedJsField(val name: String) 
	extends AsBoolean 
		with AsNumber
		with AsString
		with AsObject
		with AsArray

object ImplicitPimps {
	implicit def stringToPimpedJsField(fieldName: String) = new PimpedJsField(fieldName)
}
