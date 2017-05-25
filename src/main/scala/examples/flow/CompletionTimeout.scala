package examples.flow

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ThrottleMode}
import akka.stream.scaladsl.Source

import scala.concurrent.TimeoutException
import scala.concurrent.duration._

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object CompletionTimeout {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("flow")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    Source(1 to 3)
      .throttle(1, 1 seconds, 1, ThrottleMode.Shaping)
      .completionTimeout(2 seconds)
      .recover { case t: TimeoutException =>
        println(s"$t")
        100
      }
      .runForeach(println)

  }
}
