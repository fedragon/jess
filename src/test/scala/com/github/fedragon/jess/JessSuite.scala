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

    val jsNull = ("6", JsNull)
    val jsStringEmpty = ("7", new JsString(""))

    val jsonFull = new JsObject(
      Seq(jsNumber, jsObject, jsArray, jsString, jsBoolean, jsNull, jsStringEmpty)
    )
  }

  test("Jess should be able to validate one rule") {    

    new Data {
      import JessPimps._
      
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
      import JessPimps._
      
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
      import JessPimps._
      
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
      import JessPimps._
      
      val result = 
        verifyThat (jsonFull) { 
          obj ( 
            "1" is 123,
            "2" is (
              "2.1" isBetween (123, 456),
              "2.2" is "AAA"
            ),
            "3" is array (
              n ?= 789,
              s ?= "CCC",
              obj (
                "3.1" is 111d,
                "3.2" is "AAA"
              ),
              array (
                222L, 333f
              ),
              b ?= false
            ),
            "4" is "BBB",
            "5" is false,
            "6" isNull,
            "7" isNull
          )
        }

      assert(result.passed === true)
    }
  }

  test("Jess should be able to validate a json string") {

    new Data {
      import JessPimps._

      val jsonString = 
        """{ 
          "1" : 123, 
          "2" : { 
            "2.1": 456.7 
          },
          "3" : [
            789,
            false,
            {
              "a" : "something",
              "b" : null,
              "c" : ""
            }
          ],
          "4" : "abcd"
        }"""

      val result = 
        verifyThat (jsonString) { 
          obj ( 
            "1" is 123,
            "2" is (
              "2.1" is 456.7
            ),
            "3" is array (
              n <= 789,
              b ?= false,
              obj (
                "a" is "something",
                "b" isNull,
                "c" isNull
              )
            ),
            "4" in "[a-z]*"
          )
        }

      assert(result.passed === true)
    }
  }

  test("Jess should crash on invalid json string") {

    new Data {
      import JessPimps._
      
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