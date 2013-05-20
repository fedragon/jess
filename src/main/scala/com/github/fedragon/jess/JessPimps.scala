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

	  def array(f: JsValueRule, g: JsValueRule*) = JsArrayRule(Seq(f) ++ g)

	  def isArray(values: Any*) = {
	    val rules =
		    values map { expected =>
		      expected match {
		        case b: Boolean => JsBooleanRule(b)
		        case s: String => JsStringRule(t => t == s)
		        case jr: JsValueRule => jr
		        case BigDecimalExtractor(bd) => JsNumberRule(n => n == bd)
		        case unknown => throw new IllegalArgumentException(s"Unsupported rule: $unknown")
		      }
		    }

	    (name, JsArrayRule(rules))
	  }

	  def asArray(seq: JsValueRule*) = (name, JsArrayRule(seq))

		private object BigDecimalExtractor {
			def unapply(v: Any): Option[BigDecimal] = {
				val result =
					v match {
						case bd: BigDecimal => bd
						case s: Short => BigDecimal(s)
						case i: Int => BigDecimal(i)
						case l: Long => BigDecimal(l)
						case d: Double => BigDecimal(d)
						case f: Float => BigDecimal(f)
						case other => throw new IllegalArgumentException(s"Invalid value: $other")
					}
				Some(result)
			}
		}
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
