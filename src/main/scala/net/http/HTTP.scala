package net.http

import java.net._
import java.io._
import org.apache.commons.httpclient._

trait HTTPHandling {
  
   def start[T](host: String, handler: HTTP => T): T = start(host, HTTP.defaultPort, handler)
  
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
   
}

object HTTP extends HTTPHandling{
  
  private val urlPattern = """(?:http://)?([^/]+)/?(.*)""".r
  private val domainPort = """([^:]+):(\d*)""".r

  val defaultPort = 80
  
  def -> (url: String): HTTPResponse = {
    url match {
      case urlPattern(domain, path) => domain match { case domainPort(dom, p) => start(domain, p.toInt, _.get(path))
                                                      case _ => start(domain, defaultPort, _.get(path))
                                                    }
      case _ => error("Not a valid url pattern")
    }
  }
  
  def get(host: String, port: Int, path: String) = start(host, port, _ get path)
  
  def head(host: String, port: Int, path: String) = start(host, port, _ head path)

  def options(host: String, port: Int, path: String) = start(host, port, _ options path)

  def delete(host: String, port: Int, path: String) = start(host, port, _ delete path)

  def trace(host: String, port: Int, path: String) = start(host, port, _ trace path)
  
}

class HTTP(client: HttpClient) {
  
  private val pathRegex = """/?(.*)""".r
  
  var headers: Map[String, String] = null
  
  private def resolvePath(p: String) = p match {
    case pathRegex(actual) => "/" + actual
    case null => "/"
  }

  private def execute(method: HttpMethod, path: String) = {
    method.setPath(resolvePath(path))
    addHeaders(method)
    val resCode = client.executeMethod(method)
    new HTTPResponse(resCode, method.getResponseHeaders().toList, method.getResponseBody())
  } 

  private def addHeaders(method: HttpMethod): Unit = {
    if(headers == null) return
    headers.foreach((tuple) => method.addRequestHeader(new Header(tuple _1, tuple _2)))
  }
  
  
  def get: HTTPResponse = get("")
  
  def get(path: String) =  execute(new methods.GetMethod, path)
  
  def head: HTTPResponse = head("") 

  def head(path: String) = execute(new methods.HeadMethod, path)

  def options: HTTPResponse = options("") 

  def options(path: String) = execute(new methods.OptionsMethod, path)

  def delete: HTTPResponse = options("") 

  def delete(path: String) = execute(new methods.DeleteMethod, path)

  def trace: HTTPResponse = trace("") 

  def trace(path: String) = execute(new methods.TraceMethod(""), path)
  
}

