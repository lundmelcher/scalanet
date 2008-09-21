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
  
  private def resolvePath(p: String) = p match {
    case pathRegex(actual) => "/" + actual
    case null => "/"
  }
  
  private def execute(method: HttpMethod) = {
    client.executeMethod(method)
    new HTTPResponse(method.getResponseHeaders().toList, method.getResponseBody())
  } 
  
  def get: HTTPResponse = get("")
  
  def get(path: String): HTTPResponse =  execute(new methods.GetMethod(resolvePath(path)))
  
  def head: HTTPResponse = head("") 

  def head(path: String): HTTPResponse = execute(new methods.HeadMethod(resolvePath(path)))

  def options: HTTPResponse = options("") 

  def options(path: String): HTTPResponse = execute(new methods.OptionsMethod(resolvePath(path)))

  def delete: HTTPResponse = options("") 

  def delete(path: String): HTTPResponse = execute(new methods.DeleteMethod(resolvePath(path)))

  def trace: HTTPResponse = trace("") 

  def trace(path: String): HTTPResponse = execute(new methods.TraceMethod(resolvePath(path)))
  
}

