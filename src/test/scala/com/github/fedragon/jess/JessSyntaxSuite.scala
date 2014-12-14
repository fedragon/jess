package com.github.fedragon.jess

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scalaz._, scalaz.Scalaz._

@RunWith(classOf[JUnitRunner])
class JessSyntaxSuite extends FunSuite {

  import JessPredef._
	import JessOps._

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

	test("should provide `is` for booleans, numbers, strings") {
		new Data {
			val result = verifyThat(jsonFull) {
				obj (
					'e is false,
					'd is "BBB",
					'a is 123
				)
			}

			assert(result.isSuccess)
		}
	}

	test("should provide `isNot` for booleans, numbers, strings") {
		new Data {
			val result = verifyThat(jsonFull) {
				obj (
					'e isNot true,
					'd isNot "AAA",
					'a isNot 999
				)
			}

			assert(result.isSuccess)
		}
	}

	test("should provide `is` for arrays, objects") {
		new Data {
			val result = verifyThat(jsonFull) {
				obj (
					'b is obj (
						'b1 is 456
					),
					'c is array (
						789
					)
				)
			}

			assert(result.isSuccess)
		}
	}

	test("should provide `in` for strings") {
		new Data {
			val result = verifyThat(jsonFull) {
				obj (
					'd in "[A-Z]{3}",
					'd in "[A-Z]{3}".r
				)
			}

			assert(result.isSuccess)
		}
	}

	test("should provide `isBetween` for numbers") {
		new Data {
			val result = verifyThat(jsonFull) {
				obj (
					'a isBetween (0, 333)
				)
			}

			assert(result.isSuccess)
		}
	}

	test("should provide `isNull` for strings or null values") {
		new Data {
			val result = verifyThat(jsonFull) {
				obj (
					'f isNull,
					'g isNull
				)
			}

			assert(result.isSuccess)
		}
	}

  test("should validate multiple rules with pimped syntax") {

    new Data {
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

      assert(result.isSuccess)
    }
  }

  test("should validate a json string") {

    new Data {
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

      assert(result.isSuccess)
    }
  }
	test("should throw an exception on invalid json string") {

		new Data {
			val jsonString =
				"""{
          "a": 123,
          "b": {
            aaaaaaaaaaa
          }
        }"""

			val result =
				verifyThat (jsonString) {
					obj (
						'a is 123
					)
				}

			assert(result.isFailure)
		}
	}

	test("should throw an exception in case of mismatch between field and rule types") {

		new Data {
			val jsonString =
				"""{
          "a": 123
        }"""

			val result =
				verifyThat (jsonString) {
					obj (
						'a is "aaa"
					)
				}

			assert(result.isFailure)
		}
	}
}
