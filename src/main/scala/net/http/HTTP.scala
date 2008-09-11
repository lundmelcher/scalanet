package net.http

import java.net._
import java.io._

object HTTP extends NetPrimitives[HTTPMethod, Response, HTTP] {
  
  def buildReq(x: HTTPMethod): String = {
    x toString
  }

  def buildResp(stringList: List[String]): Response = {
    new Response(stringList)
  }
    
  private val urlPattern = """(?:http://)?([^/]+)/?(.*)""".r
  private val domainPort = """([^:]+):(\d*)""".r

  override def defaultPort = 80
  
  def -> (url: String): Response = {
    url match {
      case urlPattern(domain, path) => domain match { case domainPort(dom, p) => HTTP.req(GET(domain, path), domain, p.toInt)
                                                      case _ => HTTP.req(GET(domain, path), domain, defaultPort)
                                                    }
      case _ => error("Not a valid url pattern")
    }
  }
  
  override def getProtocol(host: String, s: Socket): HTTP = {
    new HTTP(host, s)
  }
  
}

class HTTP(host: String, s: Socket) extends Protocol[HTTPMethod, Response](host, s) {
  
  def get: Response = {
    get("/")
  }
  def get(path: String): Response = {
    send(GET(host, path))
  }
  def send(m: HTTPMethod): Response = {
    HTTP.sendAndFlush(s, HTTP.buildReq(m))
  }
  
}

