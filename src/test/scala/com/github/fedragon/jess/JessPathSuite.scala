package com.github.fedragon.jess

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class JessPathSuite extends FunSuite {

  import JessPredef._

  trait Data {

    val jsNumber = ("1", new JsNumber(123))
    val jsObject = ("2", new JsObject(Seq(("2.1", new JsNumber(456)))))
    val jsArray =  ("3", new JsArray(Seq(new JsNumber(789))))

    val json2 = new JsObject(Seq(jsNumber, jsObject))

    val jsonFull = new JsObject(Seq(jsNumber, jsObject, jsArray))
  }

  test("JessPath.\\ should concatenate paths correctly") {
    import JessPath.root

    val path = root \ "child"
    val expected = JessPath (
      Vector("/", "child")
    )

    assert(path === expected)
  }

  test("JessPath.in should be able to extract fields from a path") {
    import JessPath.root
    import JessRule._
    
    val path = root \ "1"

    new Data {
      assert(path.in(json2) === Some(new JsNumber(123)))
    }
  }
}