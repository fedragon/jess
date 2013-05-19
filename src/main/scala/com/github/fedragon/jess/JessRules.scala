package com.github.fedragon.jess

import JessPredef._

abstract sealed class JsValueRule {
	def apply(js: JsValue): Boolean
}

case class JsNumberRule(f: JsNumber => Boolean) extends JsValueRule {
	def apply(js: JsValue): Boolean = {
		js match {
			case num: JsNumber => f(num)
			case _ => throw new IllegalArgumentException("Invalid input")
		}
	}	
}

case class JsStringRule(f: JsString => Boolean) extends JsValueRule {
	def apply(js: JsValue): Boolean = {
		js match {
			case str: JsString => f(str)
			case _ => throw new IllegalArgumentException("Invalid input")
		}
	}	
}

case class JsObjectRule(fields: Seq[Validator]) extends JsValueRule {

	def apply(js: JsValue): Boolean = {

		js match {
			case obj: JsObject =>
				fields forall { field =>
					//println(s"Matching on ${field._1}")

					obj \ field._1 match {
						case notFound: JsUndefined => 
							false
						case value @ _ =>
							//println(s"Found: $value")
							field._2(value)
					}
				}
			case _ => throw new IllegalArgumentException("Invalid input")
		}
	}
}
