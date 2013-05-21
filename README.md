## Jess
======

Validates your JSon document's data by applying a set of user-defined rules on it.

### Overview

Jess provides a DSL that allows you to create a rule set to validate the contents of a JSon document according to your own needs.
Jess works with path-based rules, which means: 
* you don't need to provide class mappings for your JSon document;
* you can implement just the rules you really care about;
* you don't need to provide rules for all the fields in your JSon document (although you can, if you want).

Jess relies on play.api.libs.json library.

Example:
```scala
  import ImplicitPimps._
  
  val jsonString = 
  """{ 
    "1" : 123, 
    "2" : { 
      "2.1": 456 
    },
    "3" : [
      789,
      false,
      {
        "a" : "something"
      }
    ]
  }"""

  val result =
    verifyThat (jsonString) { 
      obj ( 
        "1" is 123,
        "2" is (
          "2.1" is 456
        ),
        "3" is array (
          789,
          false,
          obj (
            "a" is "something"
          )
        )
      )
    }

  if(result.passed == true)
    println("Validation successful!")
  else println("Something went wrong...")
```

### How does it work?

Jess relies on rules, that are, at their core, wrappers for functions f: JsValue => Boolean.
The DSL hides this (as much as possible), providing additional methods that allow you to just provide the value you want to match against, rather than create the rule yourself. For example:

```scala
obj (
  "1" is 123
)
```

is a shortcut for

```scala
JsObjectRule("1", JsNumberRule(n => n == 123)
```

To perform your validation you can either: 

* Create your rule and use it on the fly:

```scala
val result =
  verifyThat(myJsonString) {
    obj (
      "1" is 123
    )
  }
```
* Create the rule, store it in a variable and use it later on:

```scala
  val myRule =
    obj (
      "1" is 123
    )

  ...

  val result = verifyThat(myJsonString)(myRule)
```

### Useful tips
* Rules on arrays are not index-based: if your rule wants to check if the array contains a 789 value, then this rule will be applied to any numeric value inside the array and it will be considered successful if at least one of them satisfies the rule.
* If you create a rule for a field (let's call it "my-field") but that field doesn't exist in your json document, a JsUndefined("my-field") will be returned as result of the validation;
* If you need a test different than "this equals that", which is what the "is" method expands to, in this moment you cannot use the short syntax and you have to write the rule yourself like in one of the examples above; more DSL shortcuts are on their way.

### Validation Result

To check the result of your validation result, you can use any of the following approaches, depending on your needs:

```scala
result.passed == true
```

```scala
result match {
  case Ok => // everything went well
  case Nok(fields) => // here you can understand what went wrong
}
```

### License

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
