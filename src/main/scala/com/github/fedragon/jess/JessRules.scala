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

		val results =
			js match {
				case array: JsArray =>
					rules map { rule =>
						
						val fields = filter(rule, array.value)

							fields exists { field =>
							 
								field match {
									case notFound: JsUndefined => 
										false
									case value @ _ =>
										rule(value)
								}
							}
					}
				case other => throw new IllegalArgumentException(s"Invalid input: $other")
			}

		results forall ( r => r == true )
	}

	private def filter(rule: JsValueRule, fields: Seq[JsValue]): Seq[JsValue] = {
		rule match {
			case a: JsArrayRule => fields.filter(f => f.isInstanceOf[JsArray])
			case n: JsNumberRule => fields.filter(f => f.isInstanceOf[JsNumber])
			case o: JsObjectRule => fields.filter(f => f.isInstanceOf[JsObject])
			case s: JsStringRule => fields.filter(f => f.isInstanceOf[JsString])
			case other => throw new IllegalArgumentException(s"Invalid input: $other")
		}
	}
}