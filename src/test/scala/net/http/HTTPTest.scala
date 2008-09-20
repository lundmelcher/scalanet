package net.http

import org.scalatest.junit.JUnit3Suite

class HTTPTest extends JUnit3Suite {

  def testGet {
    val url = "/index.html"
    HTTP start("www.google.no", http => {
      http get;
      http get "404"
      http get url
    })
  }
  
  def testSimpleGet {
    //val resp = HTTP -> "http://www.google.no:80/search?hl=en&q=scala+lang&btnG=Google+Search&meta="
  //val resp = HTTP -> "http://localhost:8040/web/securitybreach.do"

  //val resp = HTTP -> "http://www.google/dsb-innmelding"
    //resp.foreach(skrivUt)
    true
  }
  
  def skrivUt(s: String) {
    println(s)
  }

}
