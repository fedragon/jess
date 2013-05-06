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
      case false => Ongeldig(Map(path -> Seq(s"Rule not verified for input: ${input}")))
    }

  def and(other: JessRule) = Seq(this, other)
}

object JessRule {
  // Implicit conversions
  implicit def stringToPath(input: String) = JessPath(input)
  implicit def jsNumberToRichJsNumber (input: JsNumber) = new RichJsNumber(input)
  implicit def jsObjectToRichJsObject (input: JsObject) = new RichJsObject(input)
  implicit def jsArrayToRichJsArray   (input: JsArray)  = new RichJsArray(input)

  /**
   * Wraps the rules in a JessRules.
   * @return JessRules instance
   */
  def ensure (rule: JessRule) = new JessRuleSet(Seq(rule))

  /**
   * Wraps the rules in a JessRules.
   * @return JessRules instance
   */
  def ensure (rules: Seq[JessRule]) = new JessRuleSet(rules)

  /**
   * Creates a rule for this path.
   * @retun created rule
   */
  def that (path: JessPath)(magnet: JessRulesMagnet): magnet.Output = magnet(path)

  /**
   * Creates a rule for this path.
   * @retun created rule
   */
  def and (path: JessPath)(magnet: JessRulesMagnet): magnet.Output = magnet(path)
}

/**
 * Container for rules.
 */
class JessRuleSet(val rules: Seq[JessRule]) {

  /**
   * Executes all rules on this JsObject and collects their validation results.
   * @return collected validation results
   */
  def check(jsRoot: JsObject): Seq[ValidationResult] = {

    def fieldNotFound(path: JessPath) = Ongeldig(Map(path -> Seq(s"Field not found at path: ${path}")))
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
                case wrong @ _ => Ongeldig(Map(path -> Seq(invalidInput(wrong))))
              }
            case None => fieldNotFound(path)
          }
        case numRule: JessNumberRule =>
          path.in(jsRoot) match {
            case Some(field) =>
              field match {
                case input: JsNumber => numRule(input)
                case wrong @ _ => Ongeldig(Map(path -> Seq(invalidInput(wrong))))
              }
            case None => fieldNotFound(path)
          }
        case arrayRule: JessArrayRule =>
          path.in(jsRoot) match {
            case Some(field) =>
              field match {
                case input: JsArray => arrayRule(input)
                case wrong @ _ => Ongeldig(Map(path -> Seq(invalidInput(wrong))))
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