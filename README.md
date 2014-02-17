# Jess

Validates the data in a JSon document by applying a set of user-defined rules on it.

## Overview

You can create a rule set to validate the contents of a JSon document according to rules you define.

Works with path-based rules, which means:
* you don't need to provide class mappings for your JSon document;
* you can provide rules only for the fields you care about, not (necessarily) for all the fields in your document.

Proudly relies on the `play.api.libs.json` library.

Example:

    ```scala
    import JessPredef._
    import JessImplicits._

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
            "aa" : "something",
            "bb" : null,
            "cc" : ""
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
                'aa is "something",
                'bb isNull,
                'cc isNull
              )
            ),
            'd in "[a-z]*"
          )
        }

    if(result.passed == true) println("Validation successful!")
    else println("Something went wrong...")
    ```

## How does it work?

Relies on rules, that are, at their core, wrappers for functions `JsValue => Boolean`.
Using the built-in DSL you can just provide the value you want to match against, rather than create the rule yourself.
For example:

    ```scala
    obj (
      "1" is 123
    )
    ```

is syntactic sugar for

    ```scala
    JsObjectRule('a -> JsNumberRule(n => n == 123))
    ```

To perform your validation you can either: 

* Create your rule and use it on the fly:

    ```scala
    val result =
      verifyThat(myJsonString) {
        obj (
          'a is 123
        )
      }
    ```

* Create the rule, store it in a variable and use it later on:

    ```scala
    val myRule =
        obj (
          'a is 123
        )

    ...

    val result = verifyThat(myJsonString)(myRule)

    result match {
        case Ok => ...
        case Nok(failedValidations) => ...
    }
    ```

## Simple rules

These are rules which apply to JSon boolean, numeric or string fields.

### Boolean rule

    ```scala
    'a is false
    ```

### Numeric rule

    ```scala
    'a is 123
    ```

### String rule

    ```scala
    'a is "something"
    ```

## Composite rules

These are rules which apply to JSon arrays or objects.

### Array rule:

Rules on arrays are not index-based: if your rule wants to check if the array contains a `789` value, then this rule will be applied to any numeric value inside the array and it will be considered successful if at least one of them satisfies the rule.

    ```scala
    array (
      123,
      false
    )
    ```

### Object rule:

    ```scala
    obj (
      'a is 123,
      'b is false
    )
    ```
The validation results are folded so that you still get a single `Ok` or `Nok` (containing all the failed validations).

## Pre-defined rules (so far...)
* "fieldName" *__is__* boolean|number|string|array-rule|object-rule
  * verifies that the field value is equal to the value you provided, or that the array/object rule is verified
* "fieldName" *__isNot__* boolean|number|string|array-rule|object-rule
  * verifies that the field value is NOT equal to the value you provided, or that the array/object rule is not verified
* "fieldName" *__isNull__*
  * verifies that the field value is either null or equal to the empty string
* "fieldName" *__in__* "regular expression"
  * verifies that the field value can be matched using the regular expression string you provided

## Useful tips
* If you create a rule for a field (let's call it `my-field`) but that field doesn't exist in your json document, the validation result will contain a `JsUndefined("my-field")` value;
* If you need a rule different from any of the pre-defined ones, you can write your own using the non-DSL syntax (e.g. `'a asNum n => n > 3`)

## Validation Result

To check the result of your validation result, you can use any of the following approaches, depending on your needs:

    ```scala
    result.passed == true
    ```

or

    ```scala
    result match {
      case Ok => // everything went well
      case Nok(failedValidations) => // here you can understand what went wrong
    }
    ```
