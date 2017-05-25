package examples.flow

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}

import scala.concurrent.Future

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object MapAsync {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("flow")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    Source(1 to 10)
      .mapAsync(2)(x => Future(x * 2))
      .runForeach(println)

  }
}
