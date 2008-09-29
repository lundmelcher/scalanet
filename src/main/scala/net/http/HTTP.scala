package net.http

import java.net._
import java.io._
import org.apache.commons.httpclient._
import org.apache.commons.httpclient.cookie.CookiePolicy
import Implicits._

trait HTTPHandling {
  
   def start[T](host: String, config: ConfigOptions.Value*)(handler: HTTP => T): T = start(host, HTTP.defaultPort, config: _*)(handler)
  
   def start[T](host: String, port: Int, config: ConfigOptions.Value*)(handler: HTTP => T) = {
     val hostConfig = new HostConfiguration()
     hostConfig.setHost(host, port)
     val manager = new MultiThreadedHttpConnectionManager()
     val client = new HttpClient(manager)
     client.setHostConfiguration(hostConfig)
     val connection = client.getHttpConnectionManager
     try {
	handler(new HTTP(client, config.toList))
     }
     finally {
       manager.getConnection(hostConfig).close()
     }
  }
}

object HTTP extends HTTPHandling{
  
  private val urlPattern = """(?:http://)?([^/]+)/?(.*)""".r
  private val domainPort = """([^:]+):(\d*)""".r

  val defaultPort = 80
  
  def -> (url: String): HTTPResponse = {
    url match {
      case urlPattern(domain, path) => domain match { case domainPort(dom, p) => start(domain, p.toInt)(_.get(path))
                                                      case _ => start(domain, defaultPort)(_.get(path))
                                                    }
      case _ => error("Not a valid url pattern")
    }
  }
  
  def get(host: String, port: Int, path: Resource) = start(host, port)(_ get path)
  
  def head(host: String, port: Int, path: Resource) = start(host, port)(_ head path)

  def options(host: String, port: Int, path: Resource) = start(host, port)(_ options path)

  def delete(host: String, port: Int, path: Resource) = start(host, port)(_ delete path)

  def trace(host: String, port: Int, path: Resource) = start(host, port)(_ trace path)
  
}

class HTTP private(client: HttpClient, headers: Map[String, String], config: List[ConfigOptions.Value]) {
  
  private[http] def this(client: HttpClient, config: List[ConfigOptions.Value]) {
    this(client, Map[String, String](), config)
  }
  
  private val pathRegex = "/?(.*)".r
  
  def putHeaders(headers: Tuple2[String, String]*): HTTP = {
    val newMap = createMap(headers.toList)
    new HTTP(client, newMap, config)  
  }
  
  def createMap(tuples: List[Tuple2[String, String]]): Map[String, String] = {
    tuples match {
      case Nil => Map()
      case (key, value) :: tail => Map(key -> value) ++ createMap(tail)
    }
  }
  
  private def resolvePath(p: String) = p match {
    case pathRegex(actual) => "/" + actual
    case null => "/"
  }
  
  private def execute(method: HttpMethod, request: Resource) = {
    method.setPath(resolvePath(request.path))
    addHeaders(method)
    val resCode = client.executeMethod(method)
    new HTTPResponse(resCode, method.getResponseHeaders().toList, method.getResponseBody())
  } 

  private def addHeaders(method: HttpMethod): Unit = {
    if (config contains ConfigOptions.DO_NOT_HANDLE_COOKIES) {
      method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES)
    }
    headers.foreach((tuple) => method.addRequestHeader(new Header(tuple _1, tuple _2)))
  }
  
  def get(implicit request: Resource) =  execute(new methods.GetMethod, request)
  
  def head(implicit request: Resource) = execute(new methods.HeadMethod, request)

  def options(implicit request: Resource) = execute(new methods.OptionsMethod, request)

  def delete(implicit request: Resource) = execute(new methods.DeleteMethod, request)

  def trace(implicit request: Resource) = execute(new methods.TraceMethod(""), request)
  
  
}

