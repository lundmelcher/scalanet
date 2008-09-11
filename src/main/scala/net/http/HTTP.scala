package net.http

import java.net._
import java.io._

object HTTP extends NetPrimitives[HTTPMethod, Response, HTTP] {
  
  type Prot = HTTP
  
  def buildReq(x: HTTPMethod): String = {
    x toString
  }

  def buildResp(stringList: List[String]): Response = {
    new Response(stringList)
  }
    
  private val urlPattern = """(?:http://)?([^/]+)/?(.*)""".r
  private val domainPort = """([^:]+):(\d*)""".r
  
  def -> (url: String): Response = {
    url match {
      case urlPattern(domain, path) => domain match { case domainPort(dom, p) => HTTP.req(GET(domain, path), domain, p.toInt)
                                                      case _ => HTTP.req(GET(domain, path), domain, HTTPMethod.defaultPort)
                                                    }
      case _ => error("Not a valid url pattern")
    }
  }
  
  override def getProtocol(s: Socket): HTTP = {
    new HTTP(s)
  }
  
}

class HTTP(s: Socket) extends Protocol[HTTPMethod, Response](s) {
  
  def send(m: HTTPMethod): Response = {
    HTTP.sendAndFlush(s, HTTP.buildReq(m))
  }
  
}

