package net.ftp

import org.scalatest.junit.JUnit3Suite

class FTPTest extends JUnit3Suite {

  def testCurrentDir {
    FTP.start("ftp.uio.no", "anonymous", "fredriv@ifi.uio.no", ftp => {
      println("Current directory: " + ftp.getCurrentDirectory)
      //println(ftp.get("/welcome.msg"))
    })
  }
  
}
