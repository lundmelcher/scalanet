package net.ftp

import java.net._
import java.io._

import scala.actors._
import scala.actors.Actor._

object FTP extends NetPrimitives[FTPMethod, FTPResponse, FTP] {

  def buildReq(pkg: FTPMethod): Nothing = error("not implemented")

  def buildResp(input: BufferedInputStream): Nothing = error("not implemented")
  
  protected def getProtocol(host: String, s: Socket): FTP = new FTP(host, s)
  
  def defaultPort = 21

  def req(m: FTPMethod) : FTPResponse = {
    error("Not implemented")
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
      ftp.quit
    } finally {
      s close
    }
  }
}

class FTP(host: String, s: Socket) extends Protocol[FTPMethod, FTPResponse] {
  
  def SERVICE_READY = 220
  def SERVICE_CLOSING = 221
  def FILE_TRANSFER_COMPLETE = 226
  def ENTERING_PASSIVE_MODE = 227
  
  def USER_LOGGED_IN = 230
  def USER_OK_NEED_PWD = 331
  
  def FILE_TRANSFER_STARTING = 150
  def PATHNAME = 257
  
  val replyPattern = """(\d+) (.*)""".r
  val hostPortPattern = """.*\((\d+),(\d+),(\d+),(\d+),(\d+),(\d+)\).*""".r
  
  val in = new BufferedReader(new InputStreamReader(s.getInputStream))
  val out = new PrintWriter(new OutputStreamWriter(s.getOutputStream))

  expect(SERVICE_READY)
  
  def login(username: String, password: String) {
    sendExpect("USER " + username, USER_OK_NEED_PWD)
    sendExpect("PASS " + password, USER_LOGGED_IN)
  }
  
  def quit() {
    sendExpect("QUIT", SERVICE_CLOSING)
  }
  
  def getCurrentDirectory() : String = {
    val pathPattern = """\"([^\"]*)\".*""".r

    val msg = sendExpect("PWD", PATHNAME)
    msg match {
      case pathPattern(path) => path
      case _ => error("Wrong path format")
    }
  }
  
  def get(path: String) : String = {
    get(path, self)
    expect(FILE_TRANSFER_COMPLETE) // Transfer complete
    self.receive {
      case content: String => content
    }
  }
  
  def get(path: String, resultHandler: Actor) {
    sendExpect("PASV", ENTERING_PASSIVE_MODE) match {
      case hostPortPattern(h1, h2, h3, h4, p1, p2) => {
        val host = h1 + "." + h2 + "." + h3 + "." + h4
        val port = Integer.parseInt(p1) * 256 + Integer.parseInt(p2)
        
        val worker = new FTPWorker
        worker.start
        worker ! (host, port, resultHandler) 
        
        sendExpect("RETR " + path, FILE_TRANSFER_STARTING) // Connection opened
      }
      case _ => error("Unexpected response")
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
        if (code.toInt == expected) msg
        else if (code.toInt == FILE_TRANSFER_COMPLETE) expect(expected)
        else error("Unexpected response")
      }
      case _ => expect(expected)
    }
  }
  
  def send(p: FTPMethod): Nothing = {
    error("Not implemented")
  }
}

class FTPWorker extends Actor {
  
  def act() {
    receive {
      case (host: String, port: Int, controller: Actor) => controller ! retrieve(host, port)
      case msg => error("Unhandled message: " + msg)
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
        sb.append(line + "\n")
        line = in.readLine
      }
      println("retrieve finished")
      sb.toString
    } finally {
      s close
    }
  }
  
}
