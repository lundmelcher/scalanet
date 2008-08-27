package net.ftp

abstract class FTPMethod(val host: String, val port: Int, path: String) extends NetPrimitives {
  
}

object FTPMethod {
  val defaultPort = 21
}

class GET(host: String, port: Int, path: String) extends FTPMethod(host, port, path) {
  
}

object GET {
  def apply(host: String, port: Int, path: String) = new GET(host, port, path)
  def apply(host: String, path: String) = new GET(host, FTPMethod.defaultPort, path)
}
