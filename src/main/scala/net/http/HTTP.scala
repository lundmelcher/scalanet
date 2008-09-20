package net.http

import java.net._
import java.io._
import org.apache.commons.httpclient._

object HTTP {
  
   def start[T](host: String, handler: HTTP => T): T = start(host, defaultPort, handler)
  
   def start[T](host: String, port: Int, handler: HTTP => T) = {
     val config = new HostConfiguration()
     config.setHost(host, port)
     val manager = new MultiThreadedHttpConnectionManager()
     val client = new HttpClient(manager)
     client.setHostConfiguration(config)
     val connection = client.getHttpConnectionManager
     try {
	    handler(new HTTP(client))
	  }
	  finally {
	    manager.getConnection(config).close()
	  }
  }
  
  private val urlPattern = """(?:http://)?([^/]+)/?(.*)""".r
  private val domainPort = """([^:]+):(\d*)""".r

  def defaultPort = 80
  
  def -> (url: String): HTTPResponse = {
    url match {
      case urlPattern(domain, path) => domain match { case domainPort(dom, p) => start(domain, p.toInt, _.get(path))
                                                      case _ => start(domain, defaultPort, _.get(path))
                                                    }
      case _ => error("Not a valid url pattern")
    }
  }
  
  
}

class HTTP(client: HttpClient) {
  
  private val pathRegex = """/?(.*)""".r
  
  private def path(p: String) = p match {
    case pathRegex(actual) => "/" + actual
    case null => "/"
  }
  
  def get: HTTPResponse = {
    get("")
  }
  
  def get(path: String): HTTPResponse = {
    val get = new methods.GetMethod("/")
    client.executeMethod(get)
    val headers = get.getResponseHeaders().toList
    var body = get.getResponseBody()
    new HTTPResponse(headers, body)
  }

  
}

