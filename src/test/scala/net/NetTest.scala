package net;

import org.scalatest.FunSuite

class NetTest extends FunSuite {

  test("Addition") {
    val sum = 1 + 1
    assert(sum === 2)
    assert(sum + 2 === 4)
  }

  test("Subtraction") {
    val diff = 4 - 1
    assert(diff === 3)
    assert(diff - 2 === 1)
  }

  test("Rename") {
    import http.{HTTP=>Send,_}
    val r = Send -> "http://www.google.com"
    println(r)
  }

}

