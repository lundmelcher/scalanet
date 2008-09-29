package net.http;

object Implicits {

  implicit val defaultRequest = new Resource("")
  
  implicit def string2Request(path: String) = new Resource(path)
  
}
