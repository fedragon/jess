package com.github.fedragon.jess

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class JessPathSuite extends FunSuite {

  import JessPredef._

  trait Data {

    val jsNumber = ("1", new JsNumber(123))
    val jsObject = ("2", new JsObject(Seq(("2.1", new
     JsNumber(456)))))
    val jsArray =  ("3", new JsArray(Seq(new JsNumber(789))))

    val json2 = new JsObject(Seq(jsNumber, jsObject))

    val jsonFull = new JsObject(Seq(jsNumber, jsObject, jsArray))
  }

/*
  test("JessPath.\\ should concatenate paths correctly") {
    
    import JessPath.root

    val path = root \ "child"
    val expected = new JessPath("child")

    assert(path === expected)
  }
*/
  test("Should be possible, sooner or later") {    

    //import JessImplicits._

    val num = ("1", new RichJsNumber((js: JsNumber) => js.value == 123))
    val obj = ("2", new RichJsObject(
      Seq(
        ("2.1", new RichJsNumber((js: JsNumber) => js.value == 456))
      )
    ))

    new Data {
      val validator = 
        json ( 
          num,
          obj
        )

      assert(validator(jsonFull) === true)

      /*
      jsonFull { // implicitly passing the JsValue corresponding to the root, if it exists
        "1" {
          "2.1" {
            is(456)
          }
        }

        "3" {
          exists
        }
      }
      */
    }
  }

}