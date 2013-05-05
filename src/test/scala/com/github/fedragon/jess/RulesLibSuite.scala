package com.github.fedragon.jess

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RulesLibSuite extends FunSuite {

  import JessPredef._

  test("JessNumberRule should be able to apply rules") {
    import JessRule._

    val number = new JsNumber(123)
    assert(number.asInt === 123)
  }

  test("JessObjectRule should be able to apply rules") {
    import JessRule._

    val obj = new JsObject(Seq.empty)
    assert(obj.isEmpty === true)
  }

}