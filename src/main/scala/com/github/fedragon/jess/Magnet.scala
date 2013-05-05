package com.github.fedragon.jess

import JessPredef._

/**
 * Magnet trait for rules.
 */
sealed trait JessRulesMagnet {
  type Input <: JsValue
  type Output <: JessRule
  def apply(path: JessPath): Output
}

object JessRulesMagnet {

  // Implicit conversions
  implicit def fromJessNumberRule(f: JsNumber => Boolean) = new JessRulesMagnet {
    type Input = JsNumber
    type Output = JessNumberRule
    def apply(p: JessPath) = new JessNumberRule {
      val func = f
      val path = p
    }
  }

  implicit def fromJessObjectRule(f: JsObject => Boolean) = new JessRulesMagnet {
    type Input = JsObject
    type Output = JessObjectRule
    def apply(p: JessPath) = new JessObjectRule {
      val func = f
      val path = p
    }
  }
}