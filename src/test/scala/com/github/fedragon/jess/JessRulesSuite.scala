package com.github.fedragon.jess

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class JessSuite extends FunSuite {

  import JessPredef._

  test("JessRules should be able to extract fields from a path") {
    import JessPath.root
    import JessRule._
    
    val children = Seq (
        ("1", new JsNumber(123)),
        ("2", 
            new JsObject(
                Seq(
                    ("2.1", new JsNumber(456))
                )
            )
        )
    )
    
    val jsWithNumbers = new JsObject(children)

    val path1 = root \ "1"
    val path2 = root \ "2" \ "2.1"

    val executor = ensure(that(path1) { js: JsNumber => js.exists && js.isInt })

    assert(executor.findField(jsWithNumbers, path1) === Some(new JsNumber(123)))
    assert(executor.findField(jsWithNumbers, path2) === Some(new JsNumber(456)))
  }

  test("JessRules should be able to check a single rule in a JsObject") {

    import JessPath.root
    import JessRule._
    
    val jsWithNumbers = "{ \"1\": 123, \"2\": { \"2.1\": 456 } }"

    val path1 = root \ "1"
    val path2 = root \ "2" \ "2.1"

    val rules = ensure { 
      that(path1) { js: JsNumber => js.exists && js.isInt } 
    }

    assert(rules.check(jsWithNumbers) === Seq(Geldig()))
  }

  test("JessRules should die if the actual value doesn't match the expected type") {

    import JessPath.root
    import JessRule._
    
    val jsWithNumbers = "{ \"1\": 123, \"2\": 456 }"

    val path = root \ "2"

    val rules = ensure { 
      that(path) { js: JsObject => js.exists } 
    }

    val thrown = intercept[IllegalArgumentException] {
      rules.check(jsWithNumbers)
    }

    assert(thrown != null)
  }

  test("JessRules should be able to check a single rule in a Json string") {

    import JessPath.root
    import JessRule._
    
    val jsWithNumbers = """{ 
      "1": 123, 
      "2": { 
        "2.1": 456 
      } 
    }"""

    val path1 = root \ "1"
    val path2 = root \ "2" \ "2.1"

    val rules = ensure { 
      that(path1) { js: JsNumber => js.exists && js.isInt } 
    }

    assert(rules.check(jsWithNumbers) === Seq(Geldig()))
  }

  test("should be able to check multiple rules in a row") {

    import JessPath.root
    import JessRule._
    
    val children = Seq (
        ("1", new JsNumber(123)),
        ("2", 
            new JsObject(
                Seq(
                    ("2.1", new JsNumber(456))
                )
            )
        )
    )
    
    val jsWithNumbers = new JsObject(children)

    val path1 = root \ "1"
    val path2 = root \ "2"

    val rules = ensure { 
        that(path1) { js: JsNumber => js.exists && js.isInt }
        and (path2) { js: JsObject => js.isNotEmpty }
    }

    assert(rules.check(jsWithNumbers) === Seq(Geldig()))
  }
}
