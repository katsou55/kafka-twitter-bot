package marionete.spike.laura

/**
  * Created by carlosrodrigues on 08/02/2017.
  */

class CC[T] { def unapply(a:Any):Option[T] = Some(a.asInstanceOf[T]) }

object M extends CC[scala.collection.immutable.Map[String, Any]]
object S extends CC[String]

//val Some(M(json)) = JSON.parseFull(jsonString)
//val M(info) = json("payload")
//val S(text) = info("text")
//text

//def(jsonString: String) = {
//  import scala.util.parsing.json._
//
//  val Some(M(json)) = JSON.parseFull(jsonString)
//  val M(info) = json("payload")
//  val S(text) = info("text")
//  text
//}
