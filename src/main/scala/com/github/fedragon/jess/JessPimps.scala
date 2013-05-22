package com.github.fedragon.jess

import JessPredef._

trait AsBoolean {
	this: PimpedJsField =>
		def is(expected: Boolean) = asBool(expected)
		def isNot(expected: Boolean) = asBool(!expected)
		
		def asBool(f: Boolean) = (name, JsBooleanRule(f))
}

trait AsNumber {
	this: PimpedJsField =>
		def is(expected: BigDecimal) = asNum(n => n == expected)
		def isNot(expected: BigDecimal) = asNum(n => n != expected)

		def asNum(f: BigDecimal => Boolean) = (name, JsNumberRule(f))
}

trait AsString {
	this: PimpedJsField =>
		def is(expected: String) = asStr(s => s == expected)
		def isNot(expected: String) = asStr(s => s != expected)
		def in(regex: String) = asStr(s => s.matches(regex))
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

trait AsNull {
	this: PimpedJsField =>
		def isNull = {
			val rule = new JsValueRule {

				def apply(js: JsValue): Result[JsValue] = {
					js match {
						case JsNull => 
							Ok
						case str: JsString => 
							JsStringRule(s => s == null || s.trim == "").apply(str)
						case _ => 
							Nok(Seq(js))
					}
				}
			}

			(name, rule)
		}
}

class PimpedJsField(val name: String) 
	extends AsBoolean 
		with AsNumber
		with AsString
		with AsObject
		with AsArray 
		with AsNull

object ImplicitPimps {
	implicit def stringToPimpedJsField(fieldName: String) = new PimpedJsField(fieldName)
}
