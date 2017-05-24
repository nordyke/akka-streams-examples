package examples.graphs

import akka.Done
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source, ZipWith}
import akka.stream.{ActorMaterializer, ClosedShape, UniformFanInShape}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object FanInPartial {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("parallel")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher


    /*

┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─
                                                                                                                             │
│                             ┌────────────────────────────────────────────────┬──────────────────┐
   ┌─────────┐                │                                                │  pickMaxOfThree  │                          │
│  │  ┌────┐ ├─┐            ┌─┤                                                └──────────────────┤
   │  │ 10 │ │ │───────────▶│ │───┐      ┌────────────────┐                                       │                          │
│  │  └────┘ ├─┘            └─┤   │    ┌─┤                │                                       │
   └─────────┘                │   └───▶│ │                │                                       │                          │
│                             │        └─┤     ┌────┐     ├─┐                                     │
   ┌─────────┐                │          │     │zip1│     │ │───┐                                 │             ┌─────────┐  │
│  │  ┌────┐ ├─┐            ┌─┤        ┌─┤     └────┘     ├─┘   │     ┌────────────────┐          ├─┐         ┌─┤  ┌────┐ │
   │  │ 20 │ │ │──────────▶ │ │───────▶│ │                │     │   ┌─┤                │     ┌──▶ │ │────────▶│ │  │ 30 │ │  │
│  │  └────┘ ├─┘            └─┤        └─┤                │     └──▶│ │                │     │    ├─┘         └─┤  └────┘ │
   └─────────┘                │          └────────────────┘         └─┤     ┌────┐     ├─┐   │    │             └─────────┘  │
│                             │                                       │     │zip2│     │ │───┘    │
   ┌─────────┐                │                                     ┌─┤     └────┘     ├─┘        │                          │
│  │  ┌────┐ ├─┐            ┌─┤                 ┌─────────────────▶ │ │                │          │
   │  │ 30 │ │ │──────────▶ │ │─────────────────┘                   └─┤                │          │                          │
│  │  └────┘ ├─┘            └─┤                                       └────────────────┘          │
   └─────────┘                │                                                                   │                          │
│                             └───────────────────────────────────────────────────────────────────┘
                                                                                                                             │
│
 ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘

Created with Monodraw


      */
    val pickMaxOfThree = GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._

      val zip1 = b.add(ZipWith[Int, Int, Int](math.max _))
      val zip2 = b.add(ZipWith[Int, Int, Int](math.max _))
      zip1.out ~> zip2.in0

      UniformFanInShape(zip2.out, zip1.in0, zip1.in1, zip2.in1)

    }

    val resultSink = Sink.foreach(println)

    val g = RunnableGraph.fromGraph(GraphDSL.create(resultSink) { implicit b => sink =>
      import GraphDSL.Implicits._

      val pm3 = b.add(pickMaxOfThree)

      Source.single(10) ~> pm3.in(0)
      Source.single(20) ~> pm3.in(1)
      Source.single(30) ~> pm3.in(2)
      pm3.out ~> sink.in

      ClosedShape
    })

   val max: Future[Done] = g.run()

   max.onComplete(_ => system.terminate)
  }
}
