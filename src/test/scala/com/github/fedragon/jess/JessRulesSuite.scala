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
      val workingRule = JsNumberRule(js => js.value == 123)
      assert(workingRule(jsNumber123) === true)

      val failingRule = JsNumberRule(js => js.value == 0)
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
      val workingRule = JsStringRule(js => js.value == "Ja")
      assert(workingRule(jsStringJa) === true)

      val failingRule = JsStringRule(js => js.value == "Nee")
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
      val okNumRule = JsNumberRule(js => js.value == 123)
      val failNumRule = JsNumberRule(js => js.value == 0)

      val okStrRule = JsStringRule(js => js.value == "Ja")
      val failStrRule = JsStringRule(js => js.value == "Nee")

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