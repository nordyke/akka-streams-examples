package examples.flow

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object Map {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("flow")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    Source(1 to 3)
      .map(_ * 2)
      .runForeach(println)

    val flow = Flow[Int].map(_ * 2)
    Source(1 to 3)
    .via(flow)
    .runForeach(println)
  }
}
