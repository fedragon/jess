package com.github.fedragon.jess

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class JessRulesSuite extends FunSuite {

  import JessPredef._

  trait Data {
    val jsBooleanTrue = new JsBoolean(true)
    val jsNumber123 = new JsNumber(123)
    val jsNumber456 = new JsNumber(456)
    val jsStringJa = new JsString("Ja")
    
    val jsObject = new JsObject(
      Seq(
        ("1", jsNumber123),
        ("2", jsStringJa)
      )
    )

    val jsArray = new JsArray(
      Seq(
        jsNumber123, jsBooleanTrue, jsStringJa, jsObject, jsArray2
      )
    )

    val jsArray2 = new JsArray(
      Seq(jsNumber456))
  }

  test("JsBooleanRule should work if actual input matches expected") {

    new Data {
      val rule = JsBooleanRule(true)
      assert(rule(jsBooleanTrue) === true) 
    }
  }

  test("JsBooleanRule should fail if actual input doesn't match expected") {

    new Data {
      val rule = JsBooleanRule(false)
      assert(rule(jsBooleanTrue) === false)
    }
  }

  test("JsBooleanRule should throw exception on invalid input") {

    new Data {
      val thrown = intercept[IllegalArgumentException] {
        val rule = JsBooleanRule(false)
        val wrongInput: JsValue = jsStringJa
        rule(wrongInput)
      }
      assert(thrown != null)      
    }
  }

  test("JsNumberRule should work if actual input matches expected") {

    new Data {
      val rule = JsNumberRule(n => n == 123)
      assert(rule(jsNumber123) === true) 
    }
  }

  test("JsNumberRule should fail if actual input doesn't match expected") {

    new Data {
      val rule = JsNumberRule(n => n == 0)
      assert(rule(jsNumber123) === false)
    }
  }

  test("JsNumberRule should throw exception on invalid input") {

    new Data {
      val thrown = intercept[IllegalArgumentException] {
        val rule = JsNumberRule(n => n == 123)
        val wrongInput: JsValue = jsStringJa
        rule(wrongInput)
      }
      assert(thrown != null)      
    }
  }

  test("JsStringRule should work if actual input matches expected") {

    new Data {
      val rule = JsStringRule(s => s == "Ja")
      assert(rule(jsStringJa) === true)   
    }
  }

  test("JsStringRule should fail if actual input doesn't match expected") {

    new Data {
      val rule = JsStringRule(s => s == "Nee")
      assert(rule(jsStringJa) === false)
    }
  }

  test("JsStringRule should throw exception on invalid input") {

    new Data {
      val thrown = intercept[IllegalArgumentException] {
        val rule = JsStringRule(s => s == "Ja")
        val wrongInput: JsValue = jsNumber123
        rule(wrongInput)
      }
      assert(thrown != null)      
    }
  }

  test("JsObjectRule should work if actual input matches expected") {

    new Data {
      val numRule = JsNumberRule(n => n == 123)
      val strRule = JsStringRule(s => s == "Ja")

      val objRule = JsObjectRule(Seq(("1", numRule), ("2", strRule)))
      assert(objRule(jsObject) === true)
    }
  }

  test("JsObjectRule should fail if at least one rule fails") {

    new Data {
      val okNumRule = JsNumberRule(n => n == 123)
      val failStrRule = JsStringRule(s => s == "Nee")

      val objRule = JsObjectRule(Seq(("1", okNumRule), ("2", failStrRule)))
      assert(objRule(jsObject) === false)
    }
  }

  test("JsObjectRule should fail if at least one field doesn't exist") {

    new Data {
      val okNumRule = JsNumberRule(n => n == 123)

      val objRule = JsObjectRule(Seq(("1", okNumRule), ("99", okNumRule)))
      assert(objRule(jsObject) === false)
    }
  }

  test("JsObjectRule should throw exception on invalid input") {

    new Data {
      val thrown = intercept[IllegalArgumentException] {
        val objRule = JsObjectRule(Seq(("1", JsNumberRule(n => n == 123))))
        val wrongInput: JsValue = jsNumber123
        objRule(wrongInput)
      }
      assert(thrown != null)      
    }
  }

  test("JsArrayRule should work if actual input matches expected") {    

    new Data {
      val numRule123 = JsNumberRule(n => n == 123)
      val boolRule = JsBooleanRule(true)
      val strRule = JsStringRule(s => s == "Ja")

      val arrayRule = JsArrayRule(Seq(numRule123, strRule, boolRule))
      assert(arrayRule(jsArray) === true)
    }
  }

  test("JsArrayRule should fail if at least one rule fails") {    

    new Data {
      val failNumRule = JsNumberRule(n => n == 0)
      val okStrRule = JsStringRule(s => s == "Ja")

      val arrayRule = JsArrayRule(Seq(failNumRule, okStrRule))
      assert(arrayRule(jsArray) === false)
    }
  }

  test("JsArrayRule should throw exception on invalid input") {    

    new Data {
      val thrown = intercept[IllegalArgumentException] {
        val arrayRule = JsArrayRule(Seq(JsNumberRule(n => n == 123)))
        val wrongInput: JsValue = jsNumber456
        arrayRule(wrongInput)
      }
      assert(thrown != null)      
    }
  }
}