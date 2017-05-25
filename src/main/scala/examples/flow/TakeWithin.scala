package examples.flow

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, ThrottleMode}

import scala.concurrent.duration._

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object TakeWithin {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("flow")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    Source(1 to 3)
      .throttle(1, 1.seconds, 1, ThrottleMode.Shaping)
      .takeWithin(2.seconds)
      .runForeach(println)

  }
}
