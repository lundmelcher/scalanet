package net.http

trait ListToString {

  protected val stringList: List[String]
  
  override def toString = stringIfy(stringList)
  
  def stringIfy(l: List[String]) = l.mkString("\n")
  
}
