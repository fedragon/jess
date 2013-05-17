package com.github.fedragon.jess

import JessPredef._

case class JessPath(node: String, rule: Option[JsValue => Boolean] = None, subpaths: Vector[JessPath] = Vector.empty) {

	def \ (child: String) = this.copy(subpaths = subpaths :+ JessPath(child))

	override def toString = node + subpaths.map(p => p.toString)
}

abstract sealed class RichJsValue {
	def apply(js: JsValue): Boolean
}

case class RichJsNumber(f: JsNumber => Boolean) extends RichJsValue {
	def apply(js: JsValue): Boolean = {
		js match {
			case num: JsNumber => f(num)
			case _ => throw new IllegalArgumentException("Invalid input")
		}
	}	
}

case class RichJsObject(fields: Seq[Validator]) extends RichJsValue {

	def apply(js: JsValue): Boolean = {

		js match {
			case obj: JsObject =>
				fields forall { field =>
					obj \ field._1 match {
						case value: JsValue => field._2(value)
						case _ => false
					}
				}
			case _ => throw new IllegalArgumentException("Invalid input")
		}
	}
}
