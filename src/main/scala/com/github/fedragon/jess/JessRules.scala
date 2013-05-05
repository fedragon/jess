package com.github.fedragon.jess

import JessPredef._

/**
 * Trait for rules.
 */
trait JessRule {
  type Input <: JsValue

  val func: Input => Boolean

  val path: JessPath

  def apply(input: Input): ValidationResult
}

object JessRule {
  // Implicit conversions
  implicit def stringToPath(input: String) = JessPath(input)
  implicit def jsNumberToRichJsNumber(input: JsNumber) = new RichJsNumber(input)
  implicit def jsObjectToRichJsObject(input: JsObject) = new RichJsObject(input)

  /**
   * Wraps the rules in a JessRules.
   * @return JessRules instance
   */
  def ensure (rules: JessRule*) = new JessRules(rules)

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
class JessRules(val rules: Seq[JessRule]) {

  /**
   * Executes all rules on this JsObject and collects their validation results.
   * @return collected validation results
   */
  def check(jsRoot: JsObject): Seq[ValidationResult] = {

    Seq(rules: _*).map { rule =>

      rule match {
        case objRule: JessObjectRule =>
          findField(jsRoot, objRule.path) match {
            case Some(field) =>
              field match {
                case input: JsObject => objRule(input)
                case wrong @ _ => throw new IllegalArgumentException(s"Invalid input: ${wrong}")
              }
            case None => Ongeldig(Map.empty)
          }
        case numRule: JessNumberRule =>
          findField(jsRoot, numRule.path) match {
            case Some(field) =>
              field match {
                case input: JsNumber => numRule(input)
                case wrong @ _ => throw new IllegalArgumentException(s"Invalid input: ${wrong}")
              }
            case None => Ongeldig(Map.empty)
          }
      }
    }
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

  private[jess] def findField(root: JsObject, path: JessPath): Option[JsValue] = {

    def find(node: JsObject, p: JessPath): Option[JsValue] = {

      def filter(seq: Seq[(String, JsValue)], needle: String)(f: JsValue => Option[JsValue]): Option[JsValue] = {
        val children = seq.filter(f => f._1 == needle)

        if(children.isEmpty) None
        else {
          children.head match {
            case (_, obj: JsValue) => f(obj)
            case _ => None
          }
        }
      }
      
      val thePath = p

      if (thePath.isEmpty) None
      else if(thePath.size == 1) {
        filter(node.fields, thePath.head) { obj => Some(obj) }
      }
      else {
        filter(node.fields, thePath.head) { value => 
          value match {
            case obj: JsObject => find(obj, thePath.tail)
            case _ => throw new IllegalArgumentException("Invalid path")
          }
          
        }
        
      }
    }
    
    find(root, path.tail)
  }
}