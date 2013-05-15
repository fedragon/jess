package com.github.fedragon.jess

import JessPredef._

/**
 * Trait for rules.
 */
trait JessRule {
  type Input <: JsValue

  val func: Input => Boolean

  val path: JessPath

  def apply(input: Input): ValidationResult =
    func(input) match {
      case true => Geldig(path)
      case false => Ongeldig(path, Seq(s"Rule not verified for input: ${input}"))
    }

  def +: (other: JessRule) = Vector(this, other)

  def +: (other: Vector[JessRule]) = other :+ this
}

object JessRule {
  // Implicit conversions
  implicit def jsStringToRichJsString (input: JsString) = new RichJsString(input)
  implicit def jsNumberToRichJsNumber (input: JsNumber) = new RichJsNumber(input)
  implicit def jsObjectToRichJsObject (input: JsObject) = new RichJsObject(input)
  implicit def jsArrayToRichJsArray   (input: JsArray)  = new RichJsArray(input)

  /**
   * Wraps the rules in a JessRules.
   * @return JessRules instance
   */
  def ensure (rule: JessRule) = new JessRuleSet(Vector(rule))

  /**
   * Wraps the rules in a JessRules.
   * @return JessRules instance
   */
  def ensure (rules: Vector[JessRule]) = new JessRuleSet(rules)

  def obj(p: JessPath)(f: JsObject => Boolean): JessObjectRule = {
    new JessObjectRule {
      val func = f
      val path = p
    }
  }

  def number(p: JessPath)(f: JsNumber => Boolean): JessNumberRule = {
    new JessNumberRule {
      val func = f
      val path = p
    }
  }

  def string(p: JessPath)(f: JsString => Boolean): JessStringRule = {
    new JessStringRule {
      val func = f
      val path = p
    }
  }

  def array(p: JessPath)(f: JsArray => Boolean): JessArrayRule = {
    new JessArrayRule {
      val func = f
      val path = p
    }
  }
}

/**
 * Container for rules.
 */
class JessRuleSet(val rules: Vector[JessRule]) {

  /**
   * Executes all rules on this JsObject and collects their validation results.
   * @return collected validation results
   */
  def check(jsRoot: JsObject): Seq[ValidationResult] = {

    def fieldNotFound(path: JessPath) = Ongeldig(path, Seq(s"Field not found at path: ${path}"))
    def invalidInput(input: JsValue) = s"Invalid input: ${input}"
    def unsupportedRule(rule: JessRule) = s"Unsupported rule: ${rule}"

    val result = rules.map { rule =>
      val path = rule.path

      rule match {
        case objRule: JessObjectRule =>
          path.in(jsRoot) match {
            case Some(field) =>
              field match {
                case input: JsObject => objRule(input)
                case wrong @ _ => Ongeldig(path, Seq(invalidInput(wrong)))
              }
            case None => fieldNotFound(path)
          }

        case arrayRule: JessArrayRule =>
          path.in(jsRoot) match {
            case Some(field) =>
              field match {
                case input: JsArray => arrayRule(input)
                case wrong @ _ => Ongeldig(path, Seq(invalidInput(wrong)))
              }
            case None => fieldNotFound(path)
          }

        case numRule: JessNumberRule =>
          path.in(jsRoot) match {
            case Some(field) =>
              field match {
                case input: JsNumber => numRule(input)
                case wrong @ _ => Ongeldig(path, Seq(invalidInput(wrong)))
              }
            case None => fieldNotFound(path)
          }
          
        case stringRule: JessStringRule =>
          path.in(jsRoot) match {
            case Some(field) =>
              field match {
                case input: JsString => stringRule(input)
                case wrong @ _ => Ongeldig(path, Seq(invalidInput(wrong)))
              }
            case None => fieldNotFound(path)
          }

        case rule @ _ => throw new IllegalArgumentException(unsupportedRule(rule))
      }
    }

    result.toSeq
  }

  /**
   * Executes all rules on this Json document and collects their validation results. 
   * Throws an IllegalArgumentException if it cannot convert this Json string in a valid
   * Json document.
   * @return collected validation results
   */
  def check(jsonString: String): Seq[ValidationResult] = {

    val jsRoot: JsValue = JessPredef.parse(jsonString)

    jsRoot match {
      case obj: JsObject => check(obj)
      case _ => throw new IllegalArgumentException(s"Invalid input: ${jsonString}")
    }
  }
}