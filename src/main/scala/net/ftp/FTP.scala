package net.ftp

import java.net._
import java.io._

object FTP extends NetPrimitives[FTPMethod, FTPResponse] {

  type Prot = FTP
  
  def buildReq(pkg: FTPMethod): Nothing = error("not implemented")

  def buildResp(stringList: List[String]): Nothing = error("not implemented")
  
  protected def getProtocol(s: Socket): FTP = new FTP(s)
  
  
  val replyPattern = """(\d+) (.*)""".r
  
  def req(m: FTPMethod) : FTPResponse = {
    val cmdSocket = new Socket(m.host, m.port)
    val cmdOut = new PrintWriter(new OutputStreamWriter(cmdSocket.getOutputStream))
    val cmdIn = new BufferedReader(new InputStreamReader(cmdSocket.getInputStream))
    
    execute("USER anonymous", 331, cmdOut, cmdIn)
    execute("PASS fredriv@ifi.uio.no", 230, cmdOut, cmdIn)
    execute("PWD", 257, cmdOut, cmdIn)
    null
  }

  def execute(cmd: String, expected: Int, out: Writer, in: BufferedReader) : Option[String] = {
    out write cmd
    in.readLine match {
      case replyPattern(code, msg) => {
        println(code + " " + msg)
        if (expected == code.toInt) Some(msg) else None
      }	
      case _ => None
    }
  }
  
  private val urlPattern = """(?:ftp://)?([^/]+)/?(.*)""".r
  private val hostPort = """([^:]+):(\d*)""".r

  def -> (url: String): FTPResponse = {
    url match {
      case urlPattern(domain, path) => domain match { case hostPort(host, port) => FTP.req(GET(host, port.toInt, path))
                                                      case _ => FTP.req(GET(domain, path))
                                                    }
      case _ => error("Not a valid url pattern")
    }
  }
  
}

class FTP(s: Socket) extends Protocol[FTPMethod, FTPResponse](s){
  
  override def send(p: FTPMethod): Nothing = {
    error("Not implemented")
  }
}