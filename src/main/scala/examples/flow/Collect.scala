package examples.flow

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object Collect {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("flow")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    Source(1 to 3)
      .collect {
        case x: Int if x % 2 == 0 => x * x
        case x: Int if x % 3 == 0 => x * x * x
      }
      .runForeach(println)

  }
}
