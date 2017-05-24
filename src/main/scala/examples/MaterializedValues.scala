package examples

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}

import scala.concurrent.{Future, Promise}

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object MaterializedValues {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val source: Source[Int, Promise[Option[Int]]] = Source.maybe[Int]
    val flow: Flow[Int, Int, NotUsed] = Flow[Int].map(_ * 2)
    val sink: Sink[Int, Future[Int]] = Sink.head[Int]

    val r9: RunnableGraph[((Promise[Option[Int]], NotUsed), Future[Int])] =
      source.viaMat(flow)(Keep.both).toMat(sink)(Keep.both)

    // Flatten materialized views
    val r11: RunnableGraph[(Promise[Option[Int]], NotUsed, Future[Int])] =
      r9.mapMaterializedValue {
        case((promise, notused), future) =>
          (promise, notused, future)
      }

    val (promise, notused, future) = r11.run()

    promise.success(None)
    future.map(_ + 3)

  }
}
