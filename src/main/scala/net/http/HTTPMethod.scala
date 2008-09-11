package net.http

abstract class HTTPMethod private[net](private val host: String, private val p: String) extends NetPackage {

  private val pathRegex = """/?(.*)""".r
  
  private val path = p match {
    case pathRegex(actual) => "/" + actual
    case null => "/"
  }

  val method: String
  
  override def toString = {
          """<method> <path> HTTP/1.1
             |Host: <host>
             |
             |""".stripMargin.replaceFirst("<method>", method)
                             .replaceFirst("<host>", host)
                             .replaceFirst("<path>", path)
  }
  
}

class GET(path: String, host: String) extends HTTPMethod(path, host) {
  val method = "GET"
}

object GET {
  def apply(path: String, host: String) = new GET(path, host)
}

class HEAD(host: String, path: String) extends HTTPMethod(host, path) {
  val method = "HEAD"
}

object HEAD {
  def apply(host: String, path: String) = new HEAD(host, path)
}

class POST(host: String, path: String) extends HTTPMethod(host, path) {
  val method = "POST"
}
