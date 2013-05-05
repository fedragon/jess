package com.github.fedragon.jess

import JessPredef._

/**
 * Enricher class for JsNumber.
 */
class RichJsNumber(js: JsNumber) {
  
  /**
   * Returns true if the value is not null, false otherwise.
   * @return true if the value is not null, false otherwise.
   */
  def exists: Boolean = js.value != null

  /**
   * Returns true if the value is a valid integer, false otherwise.
   * @return true if the value is a valid integer, false otherwise.
   */
  def isInt: Boolean = this.exists && js.value.isValidInt

  /**
   * Returns the value as integer or an exception if the conversion is not possible.
   * @return the value as integer or an exception if the conversion is not possible
   */
  def asInt: Int =
    if(this.isInt) js.value.toInt
    else throw new IllegalArgumentException("Not an integer value")

  /**
   * Returns true if the value is a valid double, false otherwise.
   * @return true if the value is a valid double, false otherwise.
   */
  def isDouble: Boolean = this.exists && js.value.isValidDouble

  /**
   * Returns the value as double or an exception if the conversion is not possible
   * @return the value as double or an exception if the conversion is not possible
   */
  def asDouble: Double =
    if(this.isDouble) js.value.toDouble
    else throw new IllegalArgumentException("Not a double value")
}

/**
 * Enricher class for JsObject.
 */
class RichJsObject(js: JsObject) {
  /**
   * Returns true if the value is not null, false otherwise.
   * @return true if the value is not null, false otherwise.
   */
  def exists: Boolean = js.fields != null

  /**
   * Returns true if there aren't any children, false otherwise.
   * @return true if there aren't any children, false otherwise.
   */
  def isEmpty: Boolean = this.exists && js.fields.isEmpty

  /**
   * Returns true if there are some children, false otherwise.
   * @return true if there are some children, false otherwise.
   */
  def isNotEmpty: Boolean = this.exists && !js.fields.isEmpty
}

/**
 * Enricher class for JsObject.
 */
class RichJsArray(js: JsArray) {
  /**
   * Returns true if the value is not null, false otherwise.
   * @return true if the value is not null, false otherwise.
   */
  def exists: Boolean = js.value != null

  /**
   * Returns true if there aren't any children, false otherwise.
   * @return true if there aren't any children, false otherwise.
   */
  def isEmpty: Boolean = this.exists && js.value.isEmpty

  /**
   * Returns true if there are some children, false otherwise.
   * @return true if there are some children, false otherwise.
   */
  def isNotEmpty: Boolean = this.exists && !js.value.isEmpty
}