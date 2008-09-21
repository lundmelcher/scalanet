package net.http

import org.scalatest.junit.JUnit3Suite

class HTTPTest extends JUnit3Suite {

  def testStart {
    HTTP start("www.vg.no", http => {
      http get;
      http get "/index.html"
      http head;
      http trace;
      println(http options);
    })
  }
  
  def testSimple {
    val resp = HTTP.trace("www.vg.no", 80, "/search?hl=en&q=scala+lang&btnG=Google+Search&meta=")
    println(resp)
  }
  
}
