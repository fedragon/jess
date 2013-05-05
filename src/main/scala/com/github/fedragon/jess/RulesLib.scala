package com.github.fedragon.jess

import JessPredef._

/**
 * Trait for JsNumber rules.
 */
trait JessNumberRule extends JessRule {
  type Input = JsNumber
}

/**
 * Trait for JsObject rules.
 */
trait JessObjectRule extends JessRule {
  type Input = JsObject
}

/**
 * Trait for JsArray rules.
 */
trait JessArrayRule extends JessRule {
  type Input = JsArray
}