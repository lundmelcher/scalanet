package net.http

import org.apache.commons.httpclient._

class HTTPResponse private[http](responseCode: Int, headerList: List[Header], bodyArr: Array[Byte]) {
    
  val body = new Body(bodyArr)
  val header = new InternalHeader(responseCode, headerList)
  
  override def toString = header.stringList.mkString("\n") + "\n\n"+ body.toString
  
  class Body(body: Array[Byte]) {
    override def toString = new String(body)
  }
  
  class InternalHeader(val responseCode: Int, headerList: List[Header]) extends ListToString {
    
    private val tupleList = sMap(headerList)
    val stringList = tupleList.map(str => (str._1 + ": " + str._2))
    
    private val internalMap = setupMap(tupleList)
      
    private def sMap(remaining: List[Header]): List[(String, String)] = remaining match {
      case head :: remain => (head.getName, head.getValue) :: parseElements(head.getName, head.getElements) ::: sMap(remain)
      case Nil => Nil
    }
    
    
    private def parseElements(name: String, elements: Array[HeaderElement]): List[(String, String)] = {
      if(elements == null) {
        Nil
      }
      else {
        elements.toList match {
          case head :: remain =>  {
            if(head.getValue == null) Nil else (name, head.getValue) :: parseElements(name, remain.toArray)
          }
          case Nil => Nil
        }
      }
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
      
    def getValue(key: String) = internalMap.get(key) match {
      case Some(l) => l
      case None => Nil
    }
  }
  
}

