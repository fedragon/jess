package com.github.fedragon.jess

import JessPredef._

class PimpedJsField(name: String) {

	def is(expected: Boolean) = (name, JsBooleanRule(expected))
	def is(expected: BigDecimal) = (name, JsNumberRule(n => n == expected))
	def is(expected: String) = (name, JsStringRule(s => s == expected))
	def is(seq: Validator*) = (name, JsObjectRule(seq))
	def is(rule: JsArrayRule) = (name, rule)

  def array(f: JsValueRule, g: JsValueRule*) = JsArrayRule(Seq(f) ++ g)

  def isArray(values: Any*) = {
    val rules =
	    values map { expected =>
	      expected match {
	        case b: Boolean => JsBooleanRule(b)
	        case s: String => JsStringRule(t => t == s)
	        case or: JsObjectRule => or
	        case ar: JsArrayRule => ar
	        case BigDecimalExtractor(bd) => JsNumberRule(n => n == bd)
	        case unknown => throw new IllegalArgumentException(s"Unsupported rule: $unknown")
	      }
	    }

    (name, JsArrayRule(rules))
  }

	def asBool(f: Boolean) = (name, JsBooleanRule(f))
	def asNum(f: BigDecimal => Boolean) = (name, JsNumberRule(f))
	def asStr(f: String => Boolean) = (name, JsStringRule(f))
	def asObj(seq: Validator*) = (name, JsObjectRule(seq))
	def asArray(seq: JsValueRule*) = (name, JsArrayRule(seq))

	object BigDecimalExtractor {
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

object ImplicitPimps {
	implicit def stringToPimpedJsField(fieldName: String) = new PimpedJsField(fieldName)
}
