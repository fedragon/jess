package com.github.fedragon.jess

import JessPredef._

abstract sealed class JsValueRule {
	def apply(js: JsValue): Boolean
}

case class JsNumberRule(f: BigDecimal => Boolean) extends JsValueRule {
	def apply(js: JsValue): Boolean = {
		js match {
			case num: JsNumber => f(num.value)
			case _ => throw new IllegalArgumentException("Invalid input")
		}
	}
}

case class JsStringRule(f: String => Boolean) extends JsValueRule {
	def apply(js: JsValue): Boolean = {
		js match {
			case str: JsString => f(str.value)
			case _ => throw new IllegalArgumentException("Invalid input")
		}
	}	
}

case class JsObjectRule(validators: Seq[Validator]) extends JsValueRule {

	def apply(js: JsValue): Boolean = {

		js match {
			case obj: JsObject =>
				validators forall { validator =>
					//println(s"Matching on ${field._1}")

					obj \ validator._1 match {
						case notFound: JsUndefined => 
							false
						case value @ _ =>
							//println(s"Found: $value")
							validator._2(value)
					}
				}
			case _ => throw new IllegalArgumentException("Invalid input")
		}
	}
}

case class JsArrayRule(rules: Seq[JsValueRule]) extends JsValueRule {

	def apply(js: JsValue): Boolean = {

		js match {
			case array: JsArray =>
				rules exists { rule =>
					
					array.value exists { next =>
					 
						next match {
							case notFound: JsUndefined => 
								false
							case value @ _ =>
								//println(s"Found: $value")
								rule(value)
						}
					}
				}
			case other => throw new IllegalArgumentException(s"Invalid input: $other")
		}
	}
}