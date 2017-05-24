package examples

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.{Done, NotUsed}

import scala.concurrent.Future

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object AsyncBoundary {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val done: Future[Done] = Source(List(1, 2, 3))
      .map(_ + 1).async
      .map(_ * 2)
      .runForeach(println)

    done.onComplete(_ => system.terminate())
  }
}
