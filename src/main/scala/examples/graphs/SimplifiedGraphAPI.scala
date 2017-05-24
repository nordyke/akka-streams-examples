package examples.graphs

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ClosedShape}

import scala.concurrent.Future

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object SimplifiedGraphAPI {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("simplified-graph-api")
    implicit val materializer = ActorMaterializer()

    // Fan In
    val sourceOne = Source(List(1))
    val sourceTwo = Source(List(2))
    val merged = Source.combine(sourceOne, sourceTwo)(Merge(_))

    val mergedResult: Future[Done] =
      merged
        .fold(0)(_ + _)
        .runForeach(f => println(s"FanIn: $f"))

    // Fan Out
    val sink1 = Sink.foreach[Int](s => println(s"sink1: $s"))
    val sink2 = Sink.foreach[Int](s => println(s"sink2: $s"))

    val sink = Sink.combine(sink1, sink2)(Broadcast[Int](_))

    Source(List(0, 1, 2)).runWith(sink)
  }
}
