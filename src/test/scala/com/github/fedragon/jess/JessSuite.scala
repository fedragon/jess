package com.github.fedragon.jess

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class JessSuite extends FunSuite {

  import JessPredef._

  trait Data {
    val jsNumber = ("1", new JsNumber(123))
    val jsString = ("4", new JsString("BBB"))
    val jsBoolean = ("5", new JsBoolean(false))

    val jsObject = ("2", new JsObject(
      Seq(
        ("2.1", new JsNumber(456)),
        ("2.2", new JsString("AAA"))
      ))
    )
    val jsArray =  ("3", new JsArray(
      Seq(
        new JsNumber(789), 
        new JsString("CCC"),
        new JsObject(
          Seq(
            ("3.1", new JsNumber(111d)),
            ("3.2", new JsString("AAA"))
          )),
        new JsBoolean(false),
        new JsArray(Seq(new JsNumber(222L), new JsNumber(333f)))
      ))
    )

    val jsonFull = new JsObject(
      Seq(jsNumber, jsObject, jsArray, jsString, jsBoolean)
    )
  }

  test("Jess should be able to validate one rule") {    

    new Data {
      import ImplicitPimps._
      
      val result = 
        verifyThat (jsonFull) { 
          obj ( 
            "1" asNum (n => n == 123)
          )
        }

      result match {
        case Ok => true
        case Nok(fields) => fail()
      }

    }
  }

  test("Jess should fail if at least one rule is not verified") {    

    new Data {
      import ImplicitPimps._
      
      val result = 
        verifyThat (jsonFull) { 
          obj ( 
            "4" asStr (s => s == "")
          )
        }

      assert(result.passed === false)
    }
  }

  test("Jess should be able to validate multiple rules") {    

    new Data {
      import ImplicitPimps._
      
      val result = 
        verifyThat (jsonFull) { 
          obj ( 
            "1" asNum (n => n == 123),
            "2" asObj (
              "2.1" asNum (n => n == 456),
              "2.2" asStr (s => s == "AAA")
            ),
            "3" asArray (
              JsNumberRule(n => n == 789)
            ),
            "4" asStr (s => s == "BBB"),
            "5" asBool (false)
          )
        }

      assert(result.passed === true)
    }
  }

  test("Jess should be to validate multiple rules with pimped syntax") {

    new Data {
      import ImplicitPimps._
      
      val result = 
        verifyThat (jsonFull) { 
          obj ( 
            "1" is 123,
            "2" is (
              "2.1" is 456,
              "2.2" is "AAA"
            ),
            "3" is array (
              789,
              "CCC",
              obj (
                "3.1" is 111d,
                "3.2" is "AAA"
              ),
              array (
                222L, 333f
              ),
              false
            ),
            "4" is "BBB",
            "5" is false
          )
        }

      assert(result.passed === true)
    }
  }

  test("Jess should be able to validate a json string") {

    new Data {
      import ImplicitPimps._
      
      val jsonString = 
        """{ 
          "1": 123, 
          "2": { 
            "2.1": 456 
          } 
        }"""

      val result = 
        verifyThat (jsonString) { 
          obj ( 
            "1" is 123,
            "2" is (
              "2.1" is 456
            )
          )
        }

      assert(result.passed === true)
    }
  }

  test("New DSL functions work") {

    new Data {
      import ImplicitPimps._
      
      val jsonString = 
        """{ 
          "1": 123, 
          "2": { 
            "2.1": 456,
            "2.2": "abc" 
          } 
        }"""

      val result = 
        verifyThat (jsonString) { 
          obj ( 
            "1" isNot 456,
            "2" is (
              "2.1" is 456,
              "2.2" in "^.*$",
              "2.2" isNot "def"
            )
          )
        }

      assert(result.passed === true)
    }
  }

  test("Jess should crash on invalid json string") {

    new Data {
      import ImplicitPimps._
      
      val jsonString = 
        """{ 
          "1": 123, 
          "2": { 
            aaaaaaaaaaa 
          } 
        }"""

      val thrown = intercept[IllegalArgumentException] { 
        verifyThat (jsonString) { 
          obj ( 
            "1" is 123
          )
        }
      }

      assert(thrown != null)
    }
  }
}