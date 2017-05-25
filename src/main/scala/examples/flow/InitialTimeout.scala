package examples.flow

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

import scala.concurrent.TimeoutException
import scala.concurrent.duration._

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object InitialTimeout {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("flow")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    Source(1 to 3)
      .initialDelay(1 seconds)
      .initialTimeout(500 millis)
      .recover { case t: TimeoutException =>
        println(s"$t")
        100
      }
      .runForeach(println)

  }
}
