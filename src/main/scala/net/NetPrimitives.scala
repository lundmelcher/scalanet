/*
 * NetPrimitives.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net

import java.net._
import java.io._

trait NetPrimitives[P, R, Prot <: Protocol[P, R]] {
  
  def -> (url: String): R

  def buildReq(pkg: P): String

  def buildResp(stringList: List[String]): R
  
  protected def getProtocol(s: Socket): Prot
  
  final def req(pkg: P, host: String, port: Int): R = {
    start(host, port, prot => {
	     prot send pkg 
    })
  }
  protected def doReq(pkg: P, in: BufferedReader, out: PrintWriter): R = {
	 out write buildReq(pkg)
     buildResp(readInput(in))
  }
  
  def start(host: String, port: Int, handler: Prot => R) = {
        val s = new Socket(host, port)
   	  try {
   	    val out = s.getOutputStream
        val in = s.getInputStream
	    handler(getProtocol(s))
	  }
	  finally {
	    s close()
	  }
  }
  
  def sendAndFlush(s: Socket, message: String): R = {
    val out = new PrintWriter(new BufferedOutputStream(s.getOutputStream), true)
    val in =  new BufferedReader(new InputStreamReader(s.getInputStream))
      println(message)
      out write message
      out flush()
      buildResp(readInput(in))
  }

   private def readInput(in: BufferedReader): List[String] = {
      in.readLine() match {
        case null => Nil
        case s: String => s :: readInput(in)
      }
    }
   
}

abstract class Protocol[P, R](s: Socket) {
  
  def send(p: P): R
  
}