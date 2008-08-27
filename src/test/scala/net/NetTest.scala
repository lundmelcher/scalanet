package net;

import org.scalatest.Suite

class NetTest extends Suite {

  def testAddition() {
    val sum = 1 + 1
    assert(sum === 2)
    assert(sum + 2 === 4)
  }

  def testSubtraction() {
    val diff = 4 - 1
    assert(diff === 3)
    assert(diff - 2 === 1)
  }


}

