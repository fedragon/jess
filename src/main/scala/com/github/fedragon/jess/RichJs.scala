package com.github.fedragon.jess

import JessPredef._
import scala.util.{Try, Success}

trait Exists {
  def exists: Boolean
}

trait IsEmpty extends Exists {

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

/**
 * Enricher class for JsString.
 */
class RichJsString(val js: JsString) extends IsEmpty {
  def exists: Boolean = js.value != null

  def empty: Boolean = js.value.isEmpty

  def isInt: Boolean = {
    if(!this.isEmpty) 
      Try(js.value.toInt) match {
        case Success(i) => true
        case _ => false
      }
    else false
  }

  def asInt: Int =
    if(this.isInt) js.value.toInt
    else throw new IllegalArgumentException("Not an integer value")
}

/**
 * Enricher class for JsNumber.
 */
class RichJsNumber(val js: JsNumber) extends Exists {
  def exists: Boolean = js.value != null

  def isInt: Boolean = 
    if(this.exists) 
      js.value.isValidInt
    else false

  def asInt: Int =
    if(this.isInt) js.value.toInt
    else throw new IllegalArgumentException("Not an integer value")
}

/**
 * Enricher class for JsObject.
 */
class RichJsObject(val js: JsObject) extends IsEmpty {
  def exists: Boolean = js.fields != null

  def empty: Boolean = js.fields.isEmpty
}

/**
 * Enricher class for JsObject.
 */
class RichJsArray(val js: JsArray) extends IsEmpty {
  def exists: Boolean = js.value != null
  
  def empty: Boolean = js.value.isEmpty
}