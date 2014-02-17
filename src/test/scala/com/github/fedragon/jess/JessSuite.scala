package com.github.fedragon.jess

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class JessSuite extends FunSuite {

  import JessPredef._
	import JessImplicits._

  trait Data {
    val jsNumber = ("a", new JsNumber(123))
    val jsString = ("d", new JsString("BBB"))
    val jsBoolean = ("e", new JsBoolean(false))

    val jsObject = ("b", new JsObject(
      Seq(
        ("b1", new JsNumber(456)),
        ("b2", new JsString("AAA"))
      ))
    )
    val jsArray =  ("c", new JsArray(
      Seq(
        new JsNumber(789), 
        new JsString("CCC"),
        new JsObject(
          Seq(
            ("c1", new JsNumber(111d)),
            ("c2", new JsString("AAA"))
          )),
        new JsBoolean(false),
        new JsArray(Seq(new JsNumber(222L), new JsNumber(333f)))
      ))
    )

    val jsNull = ("f", JsNull)
    val jsStringEmpty = ("g", new JsString(""))

    val jsonFull = new JsObject(
      Seq(jsNumber, jsObject, jsArray, jsString, jsBoolean, jsNull, jsStringEmpty)
    )
  }

  test("should validate a single rule") {

    new Data {
      
      val result = 
        verifyThat (jsonFull) { 
          obj ( 
            'a asNum (n => n == 123)
          )
        }

      result match {
        case Ok => true
        case Nok(fields) => fail()
      }

    }
  }

  test("should fail if at least one rule is not verified") {    

    new Data {
      val result = 
        verifyThat (jsonFull) { 
          obj ( 
            'd asStr (s => s == "")
          )
        }

      assert(result.passed === false)
    }
  }

  test("should be able to validate multiple rules") {    

    new Data {
      val result = 
        verifyThat (jsonFull) { 
          obj ( 
            'a asNum (n => n == 123),
            'b asObj (
              'b1 asNum (n => n == 456),
              'b2 asStr (s => s == "AAA")
            ),
            'c asArray (
              JsNumberRule(n => n == 789)
            ),
            'd asStr (s => s == "BBB"),
            'e asBool (false)
          )
        }

      assert(result.passed === true)
    }
  }
}
