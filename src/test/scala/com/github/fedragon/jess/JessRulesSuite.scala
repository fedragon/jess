package com.github.fedragon.jess

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class JessRulesSuite extends FunSuite {

  import JessPredef._

  trait Data {
    val jsNumber123 = new JsNumber(123)
    val jsStringJa = new JsString("Ja")
    
    val jsObject = new JsObject(
      Seq(
        ("1", jsNumber123),
        ("2", jsStringJa)
      )
    )
  }

  test("JsNumberRule works") {    

    new Data {
      val workingRule = JsNumberRule(n => n == 123)
      assert(workingRule(jsNumber123) === true)

      val failingRule = JsNumberRule(n => n == 0)
      assert(failingRule(jsNumber123) === false)

      val thrown = intercept[IllegalArgumentException] {
        val wrongInput: JsValue = jsStringJa
        workingRule(wrongInput)
      }
      assert(thrown != null)      
    }
  }

  test("JsStringRule works") {    

    new Data {
      val workingRule = JsStringRule(s => s == "Ja")
      assert(workingRule(jsStringJa) === true)

      val failingRule = JsStringRule(s => s == "Nee")
      assert(failingRule(jsStringJa) === false)

      val thrown = intercept[IllegalArgumentException] {
        val wrongInput: JsValue = jsNumber123
        workingRule(wrongInput)
      }
      assert(thrown != null)      
    }
  }

  test("JsObjectRule works") {    

    new Data {
      val okNumRule = JsNumberRule(n => n == 123)
      val failNumRule = JsNumberRule(n => n == 0)

      val okStrRule = JsStringRule(s => s == "Ja")
      val failStrRule = JsStringRule(s => s == "Nee")

      val workingRule = JsObjectRule(Seq(("1", okNumRule), ("2", okStrRule)))
      assert(workingRule(jsObject) === true)

      val failingRule = JsObjectRule(Seq(("1", failNumRule), ("2", failStrRule)))
      assert(failingRule(jsObject) === false)

      val thrown = intercept[IllegalArgumentException] {
        val wrongInput: JsValue = jsNumber123
        workingRule(wrongInput)
      }
      assert(thrown != null)      
    }
  }
}