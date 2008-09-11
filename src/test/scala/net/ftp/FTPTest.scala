package net.ftp

import org.scalatest.Suite

class FTPTest extends Suite {

  def testCurrentDir {
    FTP.start("ftp.ifi.uio.no", "anonymous", "fredriv@ifi.uio.no", ftp => {
      println("Current directory: " + ftp.getCurrentDirectory)
      //println(ftp.get("/welcome.msg"))
    })
  }
  
}
