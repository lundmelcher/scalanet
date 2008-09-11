package net.http

import org.scalatest.Suite

class HTTPTest extends Suite {

  def testGet {
    HTTP.start("www.knowit.no", http => {
      println(http.get("index.html"))
      http.get("index.php")
      http.get("knowit/Jobb")
    })
    true
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
