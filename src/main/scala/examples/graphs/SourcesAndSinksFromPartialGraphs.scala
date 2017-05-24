package examples.graphs

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source, Zip}
import akka.stream._

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object SourcesAndSinksFromPartialGraphs {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("parallel")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val pairs = Source.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._
      val zip = b.add(Zip[Int, Int]())

      def ints = Source.fromIterator(() => Iterator.from(1)).throttle(1, 1.seconds, 1, ThrottleMode.Shaping)

      ints.filter(_ % 2 != 0) ~> zip.in0
      ints.filter(_ % 2 == 0) ~> zip.in1

      SourceShape(zip.out)
    })

    val firstPair: Future[Done] = pairs.runForeach(p => println(s"firstPair: $p"))

    val pairUpWithToString = Flow.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._

      val broadcast = b.add(Broadcast[Int](2))
      val zip = b.add(Zip[Int, String]())

      broadcast.out(0).map(identity) ~> zip.in0
      broadcast.out(1).map(_.toString) ~> zip.in1

      FlowShape(broadcast.in, zip.out)
    })

    val pairUp: (NotUsed, Future[Done]) = pairUpWithToString.runWith(Source(List(1)), Sink.foreach(p => println(s"pairUp: $p")))

  }
}
