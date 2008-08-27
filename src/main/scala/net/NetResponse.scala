/*
 * NetResponse.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net

trait NetResponse {
  
  def getBody: NetBody
  
  def getHead: NetHeader

  trait NetBody {
    
  }

  trait NetHeader {
    
  }

}
