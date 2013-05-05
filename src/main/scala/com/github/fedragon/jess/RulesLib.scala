package com.github.fedragon.jess

import JessPredef._

/**
 * Trait for JsNumber rules.
 */
trait JessNumberRule extends JessRule {
  type Input = JsNumber

  def apply(input: JsNumber): ValidationResult =
    func(input) match {
      case true => Geldig()
      case false => Ongeldig(Map(path -> Seq("Failed")))
    }
}

/**
 * Trait for JsObject rules.
 */
trait JessObjectRule extends JessRule {
  type Input = JsObject

  def apply(input: JsObject): ValidationResult =
    func(input) match {
      case true => Geldig()
      case false => Ongeldig(Map(path -> Seq("Failed")))
    }
}

/**
 * Trait for JsArray rules.
 */
trait JessArrayRule extends JessRule {
  type Input = JsArray

  def apply(input: JsArray): ValidationResult =
    func(input) match {
      case true => Geldig()
      case false => Ongeldig(Map(path -> Seq("Failed")))
    }
}