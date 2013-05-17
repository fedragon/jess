package com.github.fedragon.jess

import JessPredef._

case class JessPath(node: String, rule: Option[JsValue => Boolean] = None, subpaths: Vector[JessPath] = Vector.empty) {

	def \ (child: String) = this.copy(subpaths = subpaths :+ JessPath(child))

	override def toString = node + subpaths.map(p => p.toString)
}