package net

import java.net._

abstract class Protocol[Command, Response] {
  
  def send(p: Command): Response
  
}
