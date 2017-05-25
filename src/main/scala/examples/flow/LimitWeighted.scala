package examples.flow

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object LimitWeighted {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("flow")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    // Cost per item is 2. Stop after reaching the max of 4.
    Source(1 to 5)
      .limitWeighted(4)((x) => 2)
      .runForeach(println)

  }
}
