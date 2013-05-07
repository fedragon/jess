package com.github.fedragon.jess

import JessPredef._

/**
 * Trait for JsString rules.
 */
trait JessStringRule extends JessRule {
  type Input = JsString

  override def toString = s"Rule: (path: ${path}, JsString => Boolean)"
}

/**
 * Trait for JsNumber rules.
 */
trait JessNumberRule extends JessRule {
  type Input = JsNumber

  override def toString = s"Rule: (path: ${path}, JsNumber => Boolean)"
}

/**
 * Trait for JsObject rules.
 */
trait JessObjectRule extends JessRule {
  type Input = JsObject

  override def toString = s"Rule: (path: ${path}, JsObject => Boolean)"
}

/**
 * Trait for JsArray rules.
 */
trait JessArrayRule extends JessRule {
  type Input = JsArray

  override def toString = s"Rule: (path: ${path}, JsArray => Boolean)"
}