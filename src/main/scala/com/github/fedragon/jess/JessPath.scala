package com.github.fedragon.jess

import JessPredef._

object JessPath {
  /**
   * Root
   */
  val root: JessPath = new JessPath(Vector("/"))
  
  def apply(nodes: String*) = new JessPath(Vector(nodes: _*))
}

/**
 * Represents path inside a Json.
 */
case class JessPath(path: Vector[String]) {

  def \(node: String) = new JessPath(path :+ node)
  
  def in(root: JsObject): Option[JsValue] = {

    def find(node: JsObject, p: Vector[String]): Option[JsValue] = {

      def filter(haystack: Seq[(String, JsValue)], needle: String)(f: JsValue => Option[JsValue]): Option[JsValue] = {
        val children = haystack.filter(f => f._1 == needle)

        if(children.isEmpty) None
        else {
          children.head match {
            case (_, obj: JsValue) => f(obj)
            case _ => None
          }
        }
      }

      if (p.isEmpty) None
      else if(p.size == 1) {
        filter(node.fields, p.head) { obj => Some(obj) }
      }
      else {
        filter(node.fields, p.head) { value => 
          value match {
            case obj: JsObject => find(obj, p.tail)
            case _ => throw new IllegalArgumentException("Invalid path")
          }
        }
      }
    }
    
    // The root of the path is just a placeholder, so I drop it
    find(root, path.tail)
  }

  override def toString = path.mkString("->")
}