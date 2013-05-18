package com.github.fedragon.jess

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class JessPathSuite extends FunSuite {

  import JessPredef._

  trait Data {

    val jsNumber = ("1", new JsNumber(123))
    val jsString = ("4", new JsString("Prova"))
    val jsObject = ("2", new JsObject(Seq(("2.1", new
     JsNumber(456)))))
    val jsArray =  ("3", new JsArray(Seq(new JsNumber(789))))

    val json2 = new JsObject(Seq(jsNumber, jsObject))

    val jsonFull = new JsObject(Seq(jsNumber, jsObject, jsArray, jsString))
  }

  test("Should be able to validate one rule") {    

    new Data {
      import JessImplicits._
      
      val result = 
      using(jsonFull) { 
        json ( 
          "1" asNum (js => js.value == 123)
        )
      }

      assert(result === true)
    }
  }

  test("Should fail if at least one rule is not verified") {    

    new Data {
      import JessImplicits._
      
      val result = 
      using(jsonFull) { 
        json ( 
          "5" is ""
        )
      }

      assert(result === false)
    }
  }

  test("Should be able to validate multiple rules") {    

    new Data {
      import JessImplicits._
      
      val result = 
      using(jsonFull) { 
        json ( 
          "1" asNum (js => js.value == 123),
          "2" asObj (Seq.empty),
          "4" asStr (js => js.value == "Prova")
        )
      }

      assert(result === true)
    }
  }

  test("Should be to validate multiple rules with pimped syntax") {

    new Data {
      import JessImplicits._
      
      val result = 
      using(jsonFull) { 
        json ( 
          "1" is 123,
          "4" is "Prova"
        )
      }

      assert(result === true)
    }
  }

}