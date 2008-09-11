package net.http

import org.scalatest.Suite

class HTTPTest extends Suite {

  def testGet {
    val url = "index.php"
    HTTP start("www.knowit.no", http => {
      http.get
      http.get(url)
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
