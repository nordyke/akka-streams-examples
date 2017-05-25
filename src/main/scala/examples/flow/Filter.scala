package examples.flow

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object Filter {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("flow")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    Source(1 to 3)
      .filter(_ > 2)
      .runForeach(println)

    val flow = Flow[Int].filter(_ > 2)
    Source(1 to 3)
    .via(flow)
    .runForeach(println)
  }
}
