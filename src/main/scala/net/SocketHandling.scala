/*
 * NetPrimitives.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net

import java.net._
import java.io._

trait SocketHandling[Command, Response, Prot <: Protocol[Command, Response]] {
  
  def -> (url: String): Response

  def buildReq(pkg: Command): String

  def buildResp(input: BufferedInputStream): Response
  
  protected def getProtocol(host: String, s: Socket): Prot

  final def req(pkg: Command, host: String, port: Int): Response = {
    start(host, port, prot => {
	     prot send pkg 
    })
  }
  protected def doReq(pkg: Command, in: BufferedInputStream, out: PrintWriter): Response = {
    out write buildReq(pkg)
    buildResp(in)
  }

  protected def defaultPort: Int
  
  def start(host: String, handler: Prot => Response): Response = start(host, defaultPort, handler)
  
  def start(host: String, port: Int, handler: Prot => Response) = {
        val s = new Socket(host, port)
   	  try {
	    handler(getProtocol(host, s))
	  }
	  finally {
	    s close()
	  }
  }
  
  def sendAndFlush(s: Socket, message: String): Response = {
    val out = new PrintWriter(new BufferedOutputStream(s.getOutputStream), true)
    val in =  new BufferedInputStream(s.getInputStream)
      println(message)
      out write message
      out flush()
      buildResp(in)
  }

   private def readInput(in: BufferedReader): List[String] = {
      in.readLine() match {
        case null => Nil
        case s: String => s :: readInput(in)
      }
    }
   
}
