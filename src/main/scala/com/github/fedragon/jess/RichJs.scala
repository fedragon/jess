package com.github.fedragon.jess

import JessPredef._
import scala.util.{Try, Success}

trait Exists {
  def exists: Boolean
}

trait HasValue extends Exists {
  type Value = { def value: Any }

  val js: JsValue with Value

  def exists: Boolean = js.value != null
}

trait IsEmpty {
  this: Exists =>

  def empty: Boolean

  def isEmpty: Boolean = 
    if(this.exists) 
      empty
    else true

  def isNotEmpty: Boolean = 
    if(this.exists) 
      !empty
    else false
}

trait AsInt {
  def toInt: Int

  def isInt: Boolean

  def asInt: Int =
    if(this.isInt) toInt
    else throw new IllegalArgumentException("Not an integer value")
}

/**
 * Enricher class for JsString.
 */
class RichJsString(val js: JsString) extends HasValue with IsEmpty with AsInt {
  def empty: Boolean = js.value.isEmpty

  def toInt: Int = js.value.toInt

  def isInt: Boolean = {
    if(!this.isEmpty)
      Try(js.value.toInt) match {
        case Success(i) => true
        case _ => false
      }
    else false
  }
}

/**
 * Enricher class for JsNumber.
 */
class RichJsNumber(val js: JsNumber) extends HasValue with AsInt {
  def toInt: Int = js.value.toInt

  def isInt: Boolean = 
    if(this.exists) 
      js.value.isValidInt
    else false
}

/**
 * Enricher class for JsObject.
 */
class RichJsObject(val js: JsObject) extends Exists with IsEmpty {
  def exists: Boolean = js.fields != null

  def empty: Boolean = js.fields.isEmpty
}

/**
 * Enricher class for JsObject.
 */
class RichJsArray(val js: JsArray) extends HasValue with IsEmpty {
  def empty: Boolean = js.value.isEmpty
}