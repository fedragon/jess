package com.github.fedragon.jess

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class JessSuite extends FunSuite {

  import JessPredef._

  trait Data {
    val jsNumber = ("1", new JsNumber(123))
    
    val jsObject = ("2", new JsObject(
      Seq(
        ("2.1", new JsNumber(456)),
        ("2.2", new JsString("AAA"))
      ))
    )
    val jsArray =  ("3", new JsArray(Seq(new JsNumber(789))))
    val jsString = ("4", new JsString("BBB"))

    val jsonFull = new JsObject(Seq(jsNumber, jsObject, jsArray, jsString))
  }

  test("Should be able to validate one rule") {    

    new Data {
      import ImplicitPimps._
      
      val result = 
      using(jsonFull) { 
        json ( 
          "1" asNum (n => n == 123)
        )
      }

      assert(result === true)
    }
  }

  test("Should fail if at least one rule is not verified") {    

    new Data {
      import ImplicitPimps._
      
      val result = 
      using(jsonFull) { 
        json ( 
          "5" asStr (s => s == "")
        )
      }

      assert(result === false)
    }
  }

  test("Should be able to validate multiple rules") {    

    new Data {
      import ImplicitPimps._
      
      val result = 
      using(jsonFull) { 
        json ( 
          "1" asNum (n => n == 123),
          "2" asObj (
            "2.1" asNum (n => n == 456),
            "2.2" asStr (s => s == "AAA")
          ),
          "4" asStr (s => s == "BBB")
        )
      }

      assert(result === true)
    }
  }

  test("Should be to validate multiple rules with pimped syntax") {

    new Data {
      import ImplicitPimps._
      
      val result = 
      using(jsonFull) { 
        json ( 
          "1" is 123,
          "2" is (
            "2.1" is 456,
            "2.2" is "AAA"
          ),
          "4" is "BBB"
        )
      }

      assert(result === true)
    }
  }

}