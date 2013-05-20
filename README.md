## Jess
======

Minimal Scala library for creating and running user-defined data validation rules on JSon documents.

### Overview

Jess provides a DSL that allows you to create a rule set to validate the contents of a JSon document according to your own needs.
Jess works with path-based rules, which means: 
* you don't need to provide class mappings for your JSon document;
* you can implement just the rules you really care about;
* you don't need to provide rules for all the fields in your JSon document (although you can, if you want).

Jess relies on play.api.libs.json library.

Example:
```scala
test("Validate my Json string") {
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
    using(jsonString) { 
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

  assert(result === true)
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