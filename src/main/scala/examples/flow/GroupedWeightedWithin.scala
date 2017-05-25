package examples.flow

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, ThrottleMode}

import scala.concurrent.duration._

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object GroupedWeightedWithin {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("flow")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    Source(1 to 3)
      .throttle(1, 1.seconds, 1, ThrottleMode.Shaping)
      // weight of each is 2. max is 4.
      .groupedWeightedWithin(4, 2.seconds)(x => 2)
      .runForeach(println)

  }
}
