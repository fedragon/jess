package com.github.fedragon.jess

import JessPredef._
import scala.util.matching.Regex

trait JsBooleanOps {
	this: PimpedJsField =>
		def is(expected: Boolean) = asBool(expected)
		def isNot(expected: Boolean) = asBool(!expected)

		def asBool(f: Boolean) = (name, JsBooleanRule(f))
}

trait JsNumberOps {
	this: PimpedJsField =>
		def is(expected: BigDecimal) = asNum(_ == expected)
		def isNot(expected: BigDecimal) = asNum(_ != expected)

		def isLessThan(expected: BigDecimal) = asNum(_ < expected)
		def isLessOrEqual(expected: BigDecimal) = asNum(_ <= expected)
		def isGreaterThan(expected: BigDecimal) = asNum(_ > expected)
		def isGreaterOrEqual(expected: BigDecimal) = asNum(_ >= expected)
		def isBetween(lowerBound: BigDecimal, upperBound: BigDecimal) =
      asNum(n => n >= lowerBound && n <= upperBound)

		def asNum(f: BigDecimal => Boolean) = (name, JsNumberRule(f))
}

trait JsStringOps {
	this: PimpedJsField =>
		def is(expected: String) = asStr(s => s == expected)
		def isNot(expected: String) = asStr(s => s != expected)
		def in(regex: String) = asStr(s => s.matches(regex))
		def in(regex: Regex) = asStr(s => regex.findFirstIn(s).nonEmpty)

		def asStr(f: String => Boolean) = (name, JsStringRule(f))
}

trait JsArrayOps {
	this: PimpedJsField =>
		def is(rule: JsArrayRule) = (name, rule)

	  def asArray(seq: JsValueRule*) = (name, JsArrayRule(seq))
}

trait JsObjectOps {
	this: PimpedJsField =>
		def is(seq: Validator*) = (name, JsObjectRule(seq))
		def is(seq: JsObjectRule) = (name, seq)

		def asObj(seq: Validator*) = (name, JsObjectRule(seq))
}

trait JsNullOps {
	this: PimpedJsField =>
		def isNull = {
			val rule = new JsValueRule {
				def apply(js: JsValue): Result[JsValue] = {
					js match {
						case JsNull => Ok
						case str: JsString =>
							JsStringRule(s => s == null || s.trim == "").apply(str)
						case _ => Nok(Seq(js))
					}
				}
			}

			(name, rule)
		}
}

class PimpedJsField(val name: Symbol)
	extends JsBooleanOps
		with JsNumberOps
		with JsStringOps
		with JsObjectOps
		with JsArrayOps
		with JsNullOps

object JessOps {
	implicit def symbolToPimpedJsField(fieldName: Symbol) = new PimpedJsField(fieldName)
}
