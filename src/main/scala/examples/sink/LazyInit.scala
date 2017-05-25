package examples.sink

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.Future

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object LazyInit {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("sink")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val source = Source(1 to 3)
    source.runWith(Sink.lazyInit(
      i => Future.successful(Sink.foreach(println)),
      () => NotUsed))
  }
}
