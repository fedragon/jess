## Jess
======

Minimal Scala library for creating and running user-defined data validation rules on JSon documents.

### Overview

Jess provides a DSL that allows you to create rules to validate the contents of a JSon document according to your own needs.
Please note that this validation is not in terms of JSon syntax! Jess allows you to execute custom assertions on the data inside your JSon input by taking
a number of user-defined rules and applying them: a rule is a function that is applied to a json path and returns the result of the validation.
Jess relies on play.api.libs.json library.

Example:
```scala
test("should be able to check a single rule") {
  import JessPath.root
  import JessRule._
  
  val jsWithNumbers = 
  """{ 
    "1": 123, 
    "2": { 
      "2.1": 456 
    } 
  }"""

  val path1 = root \ "1"
  val path2 = root \ "2"

  val rules = ensure { 
      that (path1) { js: JsNumber => js.isInt }
      and  (path2) { js: JsObject => js.isNotEmpty }
  }

  assert(rules.check(jsWithNumbers) === Seq(Geldig()))
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