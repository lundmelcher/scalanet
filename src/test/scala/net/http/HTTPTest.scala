package net.http

import org.scalatest.junit._

class HTTPTest extends JUnit3Suite {

  def testStart {
    HTTP start("www.vg.no", http => {
      http get;
      http get "/index.html"
      http head;
      http trace;
      http options;
    })
  }
  
  def testSimple {
    HTTP.trace("www.vg.no", 80, "/search?hl=en&q=scala+lang&btnG=Google+Search&meta=")
  }
  
  def testConnectionClose {
    HTTP.start("www.vg.no", http => {
      http.headers = Map("Connection" -> "close")
      val res = http.options;
      val List(conection) = res.header.getValue("Connection")
      assert("close" == conection)
      println(http.options);
    })
    }
  
}
