package net.io

import java.io._

class RichBufferedInputStream(val self: BufferedInputStream) extends Proxy {
  def readLine = {
    val s = new StringBuilder
    
    var found = false
    while(!found){
      val c = self.read.toChar
      if(c == '\n'){
	found=true
      }else {
	s.append(c)
      }
    }

    val i = s.indexOf("\r")
    if(i > -1){
      s.deleteCharAt(i)
    }

    s.toString
  }
}


trait ReadLine {
  implicit def bufferedInputStream2richBufferedInputStream(b: BufferedInputStream) = new RichBufferedInputStream(b)
}
