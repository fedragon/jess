package com.github.fedragon.jess

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scalaz._, scalaz.Scalaz._

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
        ("a", jsNumber123),
        ("b", jsStringJa)
      )
    )

	  val jsArray2 = new JsArray(Seq(jsNumber456))

    val jsArray = new JsArray(
      Seq(
        jsNumber123, jsNumber456, jsBooleanTrue, jsStringJa, jsObject, jsArray2
      )
    )
  }

  test("JsBooleanRule should work if actual input matches expected") {

    new Data {
      val rule = JsBooleanRule(true)
      assert(rule(jsBooleanTrue).isSuccess)
    }
  }

  test("JsBooleanRule should fail if actual input doesn't match expected") {

    new Data {
      val rule = JsBooleanRule(false)
      assert(rule(jsBooleanTrue).isFailure)
    }
  }

  test("JsBooleanRule should throw exception on invalid input") {

    new Data {
      val rule = JsBooleanRule(false)
      val wrongInput: JsValue = jsStringJa

      assert(rule(wrongInput).isFailure)
    }
  }

  test("JsNumberRule should work if actual input matches expected") {

    new Data {
      val rule = JsNumberRule(n => n == 123)
      assert(rule(jsNumber123).isSuccess)
    }
  }

  test("JsNumberRule should fail if actual input doesn't match expected") {

    new Data {
      val rule = JsNumberRule(n => n == 0)
      assert(rule(jsNumber123).isFailure)
    }
  }

  test("JsNumberRule should throw exception on invalid input") {

    new Data {
      val rule = JsNumberRule(n => n == 123)
      val wrongInput: JsValue = jsStringJa

      assert(rule(wrongInput).isFailure)
    }
  }

  test("JsStringRule should work if actual input matches expected") {

    new Data {
      val rule = JsStringRule(s => s == "Ja")
      assert(rule(jsStringJa).isSuccess)
    }
  }

  test("JsStringRule should fail if actual input doesn't match expected") {

    new Data {
      val rule = JsStringRule(s => s == "Nee")
      assert(rule(jsStringJa).isFailure)
    }
  }

  test("JsStringRule should throw exception on invalid input") {

    new Data {
      val rule = JsStringRule(s => s == "Ja")
      val wrongInput: JsValue = jsNumber123

      assert(rule(wrongInput).isFailure)
    }
  }

  test("JsObjectRule should work if actual input matches expected") {

    new Data {
      val numRule = JsNumberRule(n => n == 123)
      val strRule = JsStringRule(s => s == "Ja")

      val objRule = JsObjectRule(Seq('a -> numRule, 'b -> strRule))
      assert(objRule(jsObject).isSuccess)
    }
  }

  test("JsObjectRule should fail if at least one rule fails") {

    new Data {
      val okNumRule = JsNumberRule(n => n == 123)
      val failStrRule = JsStringRule(s => s == "Nee")

      val objRule = JsObjectRule(Seq('a -> okNumRule, 'b -> failStrRule))
      assert(objRule(jsObject).isFailure)
    }
  }

  test("JsObjectRule should fail if at least one field doesn't exist") {

    new Data {
      val okNumRule = JsNumberRule(n => n == 123)

      val objRule = JsObjectRule(Seq('a -> okNumRule, 'z -> okNumRule))
      assert(objRule(jsObject).isFailure)
    }
  }

  test("JsObjectRule should throw exception on invalid input") {

    new Data {
      val objRule = JsObjectRule(Seq('a -> JsNumberRule(n => n == 123)))
      val wrongInput: JsValue = jsNumber123

      assert(objRule(wrongInput).isFailure)
    }
  }

  test("JsArrayRule should work if actual input matches expected") {

    new Data {
      val numRule123 = JsNumberRule(n => n == 123)
      val boolRule = JsBooleanRule(true)
      val strRule = JsStringRule(s => s == "Ja")

      val arrayRule = JsArrayRule(Seq(numRule123, strRule, boolRule))
      assert(arrayRule(jsArray).isSuccess)
    }
  }

  test("JsArrayRule should fail if at least one rule fails") {

    new Data {
      val failNumRule = JsNumberRule(n => n == 0)
      val okStrRule = JsStringRule(s => s == "Ja")

      val arrayRule = JsArrayRule(Seq(failNumRule, okStrRule))
      assert(arrayRule(jsArray).isFailure)
    }
  }

  test("JsArrayRule should throw exception on invalid input") {

    new Data {
      val arrayRule = JsArrayRule(Seq(JsNumberRule(n => n == 123)))
      val wrongInput: JsValue = jsNumber456

      assert(arrayRule(wrongInput).isFailure)
    }
  }
}
