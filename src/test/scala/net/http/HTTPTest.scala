package net.http

//import org.scalatest.Suite
import org.junit.Test
import _root_.net.http._

class HTTPTest {
  
  @Test
  def testSimpleGet {
    val resp = HTTP -> "http://www.google.no:80/search?hl=en&q=scala+lang&btnG=Google+Search&meta="
  //val resp = HTTP -> "http://localhost:8040/web/securitybreach.do"

  //val resp = HTTP -> "http://www.google/dsb-innmelding"
    resp.foreach(skrivUt)
  
  }
  
  def skrivUt(s: String) {
    println(s)
  }

}