package com.github.fedragon.jess

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class JessSuite extends FunSuite {

  import JessPredef._

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

  test("Jess should be able to validate one rule") {    

    new Data {
      import JessImplicits._
      
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

  test("Jess should fail if at least one rule is not verified") {    

    new Data {
      import JessImplicits._
      
      val result = 
        verifyThat (jsonFull) { 
          obj ( 
            'd asStr (s => s == "")
          )
        }

      assert(result.passed === false)
    }
  }

  test("Jess should be able to validate multiple rules") {    

    new Data {
      import JessImplicits._
      
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

  test("Jess should be to validate multiple rules with pimped syntax") {

    new Data {
      import JessImplicits._
      
      val result = 
        verifyThat (jsonFull) { 
          obj ( 
            'a is 123,
            'b is (
              'b1 isBetween (123, 456),
              'b2 is "AAA"
            ),
            'c is array (
              789,
              "CCC",
              obj (
                'c1 is 111d,
                'c2 is "AAA"
              ),
              array (
                222L, 333f
              ),
              false
            ),
            'd is "BBB",
            'e is false,
            'f isNull,
            'g isNull
          )
        }

      assert(result.passed === true)
    }
  }

  test("Jess should be able to validate a json string") {

    new Data {
      import JessImplicits._

      val jsonString = 
        """{ 
          "a" : 123, 
          "b" : { 
            "b1": 456.7 
          },
          "c" : [
            789,
            false,
            {
              "a" : "something",
              "b" : null,
              "c" : ""
            }
          ],
          "d" : "abcd"
        }"""

      val result = 
        verifyThat (jsonString) { 
          obj ( 
            'a is 123,
            'b is (
              'b1 is 456.7
            ),
            'c is array (
              789,
              false,
              obj (
                'a is "something",
                'b isNull,
                'c isNull
              )
            ),
            'd in "[a-z]*"
          )
        }

      assert(result.passed === true)
    }
  }

  test("Jess should crash on invalid json string") {

    new Data {
      import JessImplicits._
      
      val jsonString = 
        """{ 
          "a": 123, 
          "b": { 
            aaaaaaaaaaa 
          } 
        }"""

      val thrown = intercept[IllegalArgumentException] { 
        verifyThat (jsonString) { 
          obj ( 
            'a is 123
          )
        }
      }

      assert(thrown != null)
    }
  }
}