package examples.flow

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object MapConcat {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("flow")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    Source(1 to 3)
      .mapConcat(List(_))
      .runForeach(println)

    val flow = Flow[Int].mapConcat(List(_))
    Source(1 to 3)
    .via(flow)
    .runForeach(println)
  }
}
