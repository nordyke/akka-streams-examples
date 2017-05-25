package examples.flow

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object ZipWithIndex {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("zip")
    implicit val materializer = ActorMaterializer()

    Source(5 to 8)
      .zipWithIndex
      .runForeach(println)
  }
}
