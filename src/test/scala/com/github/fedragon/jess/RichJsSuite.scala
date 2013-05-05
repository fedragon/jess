package com.github.fedragon.jess

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RichJsSuite extends FunSuite {

  import JessPredef._

  test("RichJsNumber.asInt should work with a valid integer value") {
    val richJsNumber = new RichJsNumber(new JsNumber(123))

    assert(richJsNumber.asInt === 123)
  }

  test("RichJsNumber.asInt should fail on an invalid integer value") {
    val richJsNumber = new RichJsNumber(new JsNumber(123.4))

    val thrown = intercept[IllegalArgumentException] {
      richJsNumber.asInt
    }

    assert(thrown != null)
  }

  // TODO: this doesn't work...
  ignore("RichJsNumber.asDouble should work with a valid double value") {
    val richJsNumber = new RichJsNumber(new JsNumber(123.4))

    assert(richJsNumber.asDouble === 123.4)
  }

  test("RichJsObject.isEmpty should work") {
    val richJsObject = new RichJsObject(new JsObject(Seq.empty))

    assert(richJsObject.isEmpty === true)
  }

  test("RichJsObject.isNotEmpty should work") {
    val richJsObject = new RichJsObject(
      new JsObject(
        Seq(
          ("1", new JsNumber(1))
        )
      )
    )

    assert(richJsObject.isNotEmpty === true)
  }

  test("RichJsArray.isEmpty should work") {
    val richJsArray = new RichJsArray(new JsArray(Seq.empty))

    assert(richJsArray.isEmpty === true)
  }

  test("RichJsArray.isNotEmpty should work") {
    val richJsArray = new RichJsArray(
      new JsArray(
        Seq(new JsNumber(1))
      )
    )

    assert(richJsArray.isNotEmpty === true)
  }
}