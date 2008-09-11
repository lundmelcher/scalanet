package net.http

import java.io._

class HTTPResponse private[net](input: BufferedInputStream) extends ListToString with NetResponse{
  val (header, body)  = parseInput
  val stringList = header.stringList ::: body.stringList

  private def getLine(): String = {
    val s = new StringBuilder
    
    var found = false
    while(!found){
      val c = input.read.toChar
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
  
  private def parseInput: (Header,Body) = {
    parseInput(new Header(Nil))
  }

  private def parseInput(header: Header): (Header, Body) = {
    getLine() match {
      case null => error("Something went wrong")
      case "" => (header, readBody(header.contentLength))
      case s: String => parseInput(new Header(s :: header.stringList))
    }
  }
  
  
  def readBody(contentLength: Int): Body ={
    val bytes = new Array[Byte](contentLength)
    var len = 0
    while(len < contentLength){
      len += input.read(bytes,len,contentLength-len)
    }

    if(len != contentLength){
      error("Not implemented")
    }
    
    val body = new String(bytes).split("\n").toList
    
    new Body(body)
  }

  def getBody: Body = new Body(parse()_2)
  
  def getHead: Header = new Header(parse()_1)
  
  private def parse() = {
    var isHeader = true
    stringList.partition((line) => {
      if(isHeader && !line.trim.isEmpty) {
        true 
        }
      else if(isHeader && line.trim.isEmpty) { isHeader = false; true } else false;
      })
  }
    
  class Body(val stringList: List[String]) extends ListToString with NetBody {
  }
  
  class Header(val stringList: List[String]) extends ListToString with NetHeader {
    
    private val keyValueRegex = """([^:]+):\s*(.+)""".r
    
    private val internalMap = setupMap(sMap(stringList).reverse)
      
    private def sMap(remaining: List[String]): List[(String, String)] = remaining match {
      case keyValueRegex(key, value) :: remain => (key, value) :: sMap(remain)
      case head :: remain => sMap(remain)
      case Nil => Nil
    }

    def contentLength = getValue("Content-Length") match {
      case Some(l) => l.head.toInt
      case None => error("bork")
    }
   
    
  private def setupMap(remaining: List[(String, String)]): Map[String, List[String]] = {
    //val m = collection.mutable.Map.empty[String,List[String]] 
    var m = Map.empty[String, List[String]]
    for ((field, value) <- remaining) {
      m = m.get(field) match {
        case Some(values) => m + (field -> (value :: values))
        case None => m + (field -> List(value))
        }
      }
      m
    }
      
    def getValue(key: String) = internalMap.get(key)
  }
  
}
