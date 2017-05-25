package examples.flow

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object OrElse {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("zip")
    implicit val materializer = ActorMaterializer()

    Source.empty[Int]
      .orElse(Source(1 to 3))
      .runForeach(println)
  }
}
