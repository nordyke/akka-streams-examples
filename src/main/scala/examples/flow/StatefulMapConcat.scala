package examples.flow

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}

import scala.collection.mutable

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object StatefulMapConcat {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("flow")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    Source(1 to 3)
      .statefulMapConcat(() => List(_))
      .runForeach(println)

    val flow = Flow[Int].statefulMapConcat(() => List(_))
    Source(1 to 3)
    .via(flow)
    .runForeach(println)
  }
}
