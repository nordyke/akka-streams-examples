package examples.flow

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, ThrottleMode}

import scala.concurrent.duration._

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object Intersperse {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("flow")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    // 1, 800, 2, 800, 3
    Source(1 to 3)
      .intersperse(800)
      .runForeach(println)

    // Output is -500, 1, 0, 2, 0, 3, 500
    Source(1 to 3)
      .intersperse(-500, 0, 500)
      .runForeach(println)
  }
}
