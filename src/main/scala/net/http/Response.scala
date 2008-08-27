package net.http

class Response private[net](val stringList: List[String]) extends Seq[String] with ListToString with Sequify{

  def getBody: Body = new Body(parse()_2)
  
  def getHead: Header = new Header(parse()_1)
  
  private def parse() = {
    var isHeader = true
    stringList.partition((line) => {
      if(isHeader && !line.trim.isEmpty) {
        true 
        }
      else if(isHeader && line.trim.isEmpty) { isHeader = false; true } else false;
      })
  }
    
  class Body(val stringList: List[String]) extends Seq[String] with ListToString with Sequify {
  }
  
  class Header(val stringList: List[String]) extends Seq[String] with ListToString with Sequify{
    assume(!stringList.isEmpty)
    
    val statusLine = stringList(0)
    
    val statusInt = """\d{3}""".r.findFirstIn(statusLine).get
    
    private val keyValueRegex = """([^:]+):\s*(.+)""".r
    
    private val internalMap = setupMap(sMap(stringList).reverse)
      
    private def sMap(remaining: List[String]): List[(String, String)] = remaining match {
      case keyValueRegex(key, value) :: remain => (key, value) :: sMap(remain)
      case head :: remain => sMap(remain)
      case Nil => Nil
    }
    
  private def setupMap(remaining: List[(String, String)]): Map[String, List[String]] = {
    //val m = collection.mutable.Map.empty[String,List[String]] 
    var m = Map.empty[String, List[String]]
    for ((field, value) <- remaining) {
      m = m.get(field) match {
        case Some(values) => m + (field -> (value :: values))
        case None => m + (field -> List(value))
        }
      }
      m
    }
      
    def getValue(key: String) = internalMap.get(key)
  }
  
}
