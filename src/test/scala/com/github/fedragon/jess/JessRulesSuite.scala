package com.github.fedragon.jess

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class JessSuite extends FunSuite {

  import JessPredef._

  trait Data {

    val jsNumber = ("1", new JsNumber(123))
    val jsObject = ("2", new JsObject(Seq(("2.1", new JsNumber(456)))))
    val jsArray =  ("3", new JsArray(Seq(new JsNumber(789))))

    val json2 = new JsObject(Seq(jsNumber, jsObject))

    val jsonFull = new JsObject(Seq(jsNumber, jsObject, jsArray))
  }

  test("JessRules should be able to extract fields from a path") {
    import JessPath.root
    import JessRule._
    
    val path1 = root \ "1"
    val path2 = root \ "2" \ "2.1"

    val rules = ensure {
      that(path1) { js: JsNumber => js.exists && js.isInt }
    }

    new Data {
      assert(rules.findField(json2, path1) === Some(new JsNumber(123)))
      assert(rules.findField(json2, path2) === Some(new JsNumber(456)))
    }
  }

  test("JessRules should be able to check a single rule in a JsObject") {

    import JessPath.root
    import JessRule._

    val path1 = root \ "1"

    val rules = ensure { 
      that(path1) { js: JsNumber => js.exists && js.asInt == 123 } 
    }

    new Data {
      assert(rules.check(json2) === Seq(Geldig()))
    }
  }

  test("JessRules should fail if a rule is not verified") {

    import JessPath.root
    import JessRule._

    val path1 = root \ "1"

    val rules = ensure { 
      that(path1) { js: JsNumber => !js.exists }
    }

    new Data {
      assert(rules.check(json2) === Seq(Ongeldig(Map(path1 -> Seq("Rule not verified for input: 123")))))
    }
  }

  test("JessRules should die if the actual value doesn't match the expected type") {

    import JessPath.root
    import JessRule._
    
    val jsonNotMatching = "{ \"1\": 123, \"2\": 456 }"

    val path = root \ "2"

    val rules = ensure { 
      that(path) { js: JsObject => js.exists } 
    }

    val thrown = intercept[IllegalArgumentException] {
      rules.check(jsonNotMatching)
    }

    assert(thrown != null)
  }

  test("JessRules should be able to check a single rule in a Json string") {

    import JessPath.root
    import JessRule._
    
    val jsonString = """{ 
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

    assert(rules.check(jsonString) === Seq(Geldig()))
  }

  test("JessRules should be able to check multiple rules in a row") {

    import JessPath.root
    import JessRule._

    val path1 = root \ "1"
    val path2 = root \ "2"

    val rules = ensure { 
        that(path1) { js: JsNumber => js.exists && js.isInt }
        and (path2) { js: JsObject => js.isNotEmpty }
    }

    new Data {
      assert(rules.check(json2) === Seq(Geldig()))
    }
  }

  test("JessRules should be able to check rules on all kinds of json values") {
    
    import JessPath.root
    import JessRule._
    
    val path1 = root \ "1"
    val path2 = root \ "2"
    val path3 = root \ "3"

    val rules = ensure { 
        that(path1) { js: JsNumber => js.exists && js.isInt }
        and (path2) { js: JsObject => js.isNotEmpty }
        and (path3) { js: JsArray => js.isNotEmpty }
    }

    new Data {
      assert(rules.check(jsonFull) === Seq(Geldig()))
    }
  }
}
