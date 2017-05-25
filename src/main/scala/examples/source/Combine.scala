package examples.source

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Merge, Source}

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object Combine {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("source")
    implicit val materializer = ActorMaterializer()

    val source = Source.combine(Source.single(1), Source.single(2))(Merge(_))
    source.runForeach(println)
  }
}
