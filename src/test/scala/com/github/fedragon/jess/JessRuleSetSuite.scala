package com.github.fedragon.jess

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class JessRuleSetSuite extends FunSuite {

  import JessPredef._

  trait Data {

    val jsNumber = ("1", new JsNumber(123))
    val jsObject = ("2", new JsObject(Seq(("2.1", new JsNumber(456)))))
    val jsArray =  ("3", new JsArray(Seq(new JsNumber(789))))
    val jsString = ("4", new JsString("aaa"))

    val json2 = new JsObject(Seq(jsNumber, jsObject))

    val jsonFull = new JsObject(Seq(jsNumber, jsObject, jsArray, jsString))
  }

  test("New syntax works") {
    import JessPath.root
    import JessRule._

    val path1 = root \ "1"
    val path2 = root \ "2"
    val path3 = root \ "3"
    val path4 = root \ "4"

    val rules = ensure { 
      number(path1) { 
        js => js.exists && js.asInt == 123 
      } +: obj(path2) { 
        js => js.exists 
      } +: array(path3) { 
        js => js.exists 
      } +: string(path4) { 
        js => js.exists 
      }
    }

    new Data {
      val actual = rules.check(jsonFull)
      val expected = Vector(Geldig(path1), Geldig(path2), Geldig(path3), Geldig(path4))
      assert(actual.diff(expected) === Seq.empty)
    }
  }



  test("should be able to check a single rule in a JsObject") {

    import JessPath.root
    import JessRule._

    val path1 = root \ "1"

    val rules = ensure { 
      that(path1) { js: JsNumber => js.exists && js.asInt == 123 } 
    }

    new Data {
      assert(rules.check(json2) === Seq(Geldig(path1)))
    }
  }

  test("should fail if a path is not found") {

    import JessPath.root
    import JessRule._

    val path = root \ "9"

    val rules = ensure { 
      that(path) { js: JsNumber => true }
    }

    new Data {
      assert(rules.check(json2) === Seq(Ongeldig(path, Seq(s"Field not found at path: ${path}"))))
    }
  }

  test("should fail if a rule is not verified") {

    import JessPath.root
    import JessRule._

    val path = root \ "1"

    val rules = ensure { 
      that(path) { js: JsNumber => !js.exists }
    }

    new Data {
      assert(rules.check(json2) === Seq(Ongeldig(path, Seq("Rule not verified for input: 123"))))
    }
  }

  test("should fail (but still execute all rules) if some rules are not verified") {

    import JessPath.root
    import JessRule._

    val path1 = root \ "1"
    val path2 = root \ "2"
    val path4 = root \ "4"

    val rules = ensure { 
        that(path1) { 
          js: JsNumber => !js.exists
        } +: that(path2) { 
          js: JsObject => !js.exists
        } +: that(path4) {
          js: JsNumber => js.isInt
        }
    }

    new Data {
      val expected = Vector(
        Ongeldig(path1, Seq("Rule not verified for input: 123")),
        Ongeldig(path2, Seq("Rule not verified for input: {\"2.1\":456}")),
        Ongeldig(path4, Seq("Field not found at path: /->4"))
      )

      val actual = rules.check(json2)
      assert(actual.diff(expected) === Seq.empty)
    }
  }

  test("should fail if the actual value doesn't match the expected type") {

    import JessPath.root
    import JessRule._
    
    val mismatchingInput = new JsNumber(456)
    val mismatchingJson = new JsObject(
      Seq(("1", new JsNumber(123)), ("2", mismatchingInput))
    )

    val path = root \ "2"

    val rules = ensure { 
      that(path) { js: JsObject => js.exists } 
    }

    assert(rules.check(mismatchingJson) === Seq(Ongeldig(path, Seq(s"Invalid input: ${mismatchingInput}"))))
  }

  test("should be able to check a single rule in a Json string") {

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

    assert(rules.check(jsonString) === Seq(Geldig(path1)))
  }

  test("should be able to check multiple rules in a row") {

    import JessPath.root
    import JessRule._

    val path1 = root \ "1"
    val path2 = root \ "2"

    val rules = ensure { 
        that(path1) { 
          js: JsNumber => js.exists && js.isInt 
        } +: that(path2) { 
          js: JsObject => js.isNotEmpty 
        }
    }

    new Data {
      val expected = Seq(Geldig(path1), Geldig(path2))
      val actual = rules.check(json2)
      assert(actual.diff(expected) === Seq.empty)
    }
  }

  test("should be able to check rules on all kinds of json values") {
    
    import JessPath.root
    import JessRule._
    
    val path1 = root \ "1"
    val path2 = root \ "2"
    val path3 = root \ "3"

    val rules = ensure { 
        that(path1) { 
          js: JsNumber => js.exists && js.isInt 
        } +: that(path2) { 
          js: JsObject => js.isNotEmpty 
        } +: that(path3) { 
          js: JsArray => js.isNotEmpty 
        }
    }

    new Data {
      val expected = Seq(Geldig(path1), Geldig(path2), Geldig(path3))
      val actual = rules.check(jsonFull)
      assert(actual.diff(expected) === Seq.empty)
    }
  }
}
