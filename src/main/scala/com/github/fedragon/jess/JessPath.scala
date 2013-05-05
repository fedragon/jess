package com.github.fedragon.jess

import JessPredef._

object JessPath {
  /**
   * Root
   */
  val root: JessPath = new JessPath(Vector(""))
  
  def apply(nodes: String*) = new JessPath(Vector(nodes: _*))
}

/**
 * Represents path inside a Json.
 */
case class JessPath(path: Vector[String]) {

  /**
   * Returns a new JessPath that represents this path with the new node appended.
   * @return new JessPath that represents this path with the new node appended.
   */
  def \(node: String) = new JessPath(path :+ node)
  
  def foreach(f: Vector[String] => Unit) {
    f(path)
  }

  def isEmpty = path.isEmpty

  def size = path.size

  def head = path.head

  def tail = JessPath(path.tail)

  override def toString = path.tail.mkString("/", "~", "")
}