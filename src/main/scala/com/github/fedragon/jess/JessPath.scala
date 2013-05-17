package com.github.fedragon.jess

import JessPredef._

object JessPath {
	val root = new JessPath(Vector.empty)

	def apply(nodes: String*) = new JessPath(Vector(nodes: _*))
}

case class JessPath(nodes: Vector[String]) {

	def \ (node: String) = new JessPath(nodes :+ node)

	override def toString = nodes mkString "~>"
}