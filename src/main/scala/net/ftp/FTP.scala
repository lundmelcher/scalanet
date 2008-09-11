package net.ftp

import java.net._
import java.io._

object FTP extends NetPrimitives[FTPMethod, FTPResponse, FTP] {

  def buildReq(pkg: FTPMethod): Nothing = error("not implemented")

  def buildResp(stringList: List[String]): Nothing = error("not implemented")
  
  protected def getProtocol(host: String, s: Socket): FTP = new FTP(host, s)
  
  def defaultPort = 21

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
  
  def start(host: String, username: String, password: String, handler: FTP => Unit) {
    val s = new Socket(host, 21)
    try {
      val ftp = new FTP(host, s)
      ftp.login(username, password)
      handler(ftp)
    } finally {
      s close
    }
  }
}

class FTP(host: String, s: Socket) extends Protocol[FTPMethod, FTPResponse] {
  
  val replyPattern = """(\d+) (.*)""".r
  val hostPortPattern = """.*\((\d+),(\d+),(\d+),(\d+),(\d+),(\d+)\).*""".r
  
  val in = new BufferedReader(new InputStreamReader(s.getInputStream))
  val out = new PrintWriter(new OutputStreamWriter(s.getOutputStream))

  expect(220)
  
  def login(username: String, password: String) {
    sendExpect("USER " + username, 331)
    sendExpect("PASS " + password, 230)
  }
  
  def getCurrentDirectory() : String = {
    val pathPattern = """\"([^\"]*)\".*""".r

    val msg = sendExpect("PWD", 257)
    msg match {
      case pathPattern(path) => path
      case _ => error("Wrong path format")
    }
  }
  
  def get(path: String) : String = {
    sendExpect("PASV", 227) match {
      case hostPortPattern(h1, h2, h3, h4, p1, p2) => {
        val host = h1 + "." + h2 + "." + h3 + "." + h4
        val port = Integer.parseInt(p1) * 256 + Integer.parseInt(p2)
        send("RETR " + path)
        val resp = retrieve(host, port)
        expect(150) // Connection opened
        expect(226) // Transfer complete
        resp
      }
      case _ => error("Unexpected response")
    }
  }
  
  def retrieve(host: String, port: Int) : String = {
    println("Retrieving data from " + host + ":" + port)
    
    val s = new Socket(host, port)
    try {
      val in = new BufferedReader(new InputStreamReader(s.getInputStream))
      val sb = new StringBuffer
    
      var line = in.readLine
      while (line != null) {
        println("retrieved: " + line)
        sb.append(line + "\n")
        line = in.readLine
      }
      println("retrieve finished")
      sb.toString
    } finally {
      s close
    }
  }
  
  def sendExpect(msg: String, code: Int) : String = {
    send(msg)
    expect(code)
  }

  def send(msg: String) {
    println("-> " + msg)
    out write msg + "\n"
    out flush
  }
  
  def expect(expected: Int) : String = {
    val resp = in.readLine
    println("<- " + resp)
    resp match {
      case replyPattern(code, msg) => {
        if (Integer.parseInt(code) == expected) msg else error("Unexpected response")
      }
      case _ => expect(expected)
    }
  }
  
  def send(p: FTPMethod): Nothing = {
    error("Not implemented")
  }
}
