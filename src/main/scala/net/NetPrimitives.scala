/*
 * NetPrimitives.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net

trait NetPrimitives {
  
  type P <: NetPackage
  
  def -> (url: String): NetResponse

  def req(pkg: P): NetResponse

}