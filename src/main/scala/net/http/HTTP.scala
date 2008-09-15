package net.http

import java.net._
import java.io._

object HTTP extends SocketHandling[HTTPMethod, HTTPResponse, HTTP] {
  
  def buildReq(x: HTTPMethod): String = {
    x toString
  }

  def buildResp(input: BufferedInputStream): HTTPResponse = {
    new HTTPResponse(input)
  }
    
  private val urlPattern = """(?:http://)?([^/]+)/?(.*)""".r
  private val domainPort = """([^:]+):(\d*)""".r

  override def defaultPort = 80
  
  def -> (url: String): HTTPResponse = {
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

class HTTP(host: String, s: Socket) extends Protocol[HTTPMethod, HTTPResponse] {
  
  def get: HTTPResponse = {
    get("/")
  }
  def get(path: String): HTTPResponse = {
    send(GET(host, path))
  }
  def send(m: HTTPMethod): HTTPResponse = {
    HTTP.sendAndFlush(s, HTTP.buildReq(m))
  }
  
}

