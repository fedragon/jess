package com.github.fedragon.jess

import JessPredef._

/**
 * Enricher class for JsNumber.
 */
class RichJsNumber(js: JsNumber) {
  def exists: Boolean = js.value != null

  def isInt: Boolean = this.exists && js.value.isValidInt

  def asInt: Int =
    if(this.isInt) js.value.toInt
    else throw new IllegalArgumentException("Not an integer value")
}

/**
 * Enricher class for JsObject.
 */
class RichJsObject(js: JsObject) {
  def exists: Boolean = js.fields != null

  def isEmpty: Boolean = this.exists && js.fields.isEmpty

  def isNotEmpty: Boolean = this.exists && !js.fields.isEmpty
}

/**
 * Enricher class for JsObject.
 */
class RichJsArray(js: JsArray) {
  def exists: Boolean = js.value != null

  def isEmpty: Boolean = this.exists && js.value.isEmpty

  def isNotEmpty: Boolean = this.exists && !js.value.isEmpty
}