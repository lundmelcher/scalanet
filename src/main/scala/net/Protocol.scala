package net

import java.net._

abstract class Protocol[Command, Response](host: String, s: Socket) {
  
  def send(p: Command): Response
  
}
