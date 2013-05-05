package com.github.fedragon.jess

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class JessPathSuite extends FunSuite {

  import JessPredef._

  test("JessPath.\\ should concatenate paths correctly") {
    import JessPath.root
    import JessRule.stringToPath

    val path = root \ "child"
    val expected = JessPath (
      Vector("", "child")
    )

    assert(path === expected)
  }
}