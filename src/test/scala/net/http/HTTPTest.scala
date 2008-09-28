package net.http

import org.scalatest.junit._
import ConfigOptions._

class HTTPTest extends JUnit3Suite with HTTPHandling {

  def testMixinMethod {
    start("www.vg.no") {http => 
      http get;
      http get "/index.html"
      http head;
      http trace;
      http options;
    }
  }
  
  def testCookieHandling {
    start("www.google.no", DO_NOT_HANDLE_COOKIES) {http => 
      val resp = http get;
      val cookies = resp.header.value("Set-Cookie")
      val resp1 = http get;
      val cookies1 = resp1.header.value("Set-Cookie")
      assert(cookies != cookies1)
      assert(cookies.length == cookies1.length)
    }
  }
  
  def testSimple {
    HTTP.trace("www.vg.no", 80, "/search?hl=en&q=scala+lang&btnG=Google+Search&meta=")
  }
  
  def testConnectionClose {
    HTTP.start("www.vg.no")(http => {
      val newHttp = http putHeaders ("Connection" -> "close")
      val res = newHttp.options;
      val List(conection) = res.header.value("Connection")
      assert("close" == conection)
    })
    }
  
}