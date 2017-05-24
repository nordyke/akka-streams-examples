package examples.graphs

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.{Done, NotUsed}

import scala.concurrent.Future

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object ScatterGather {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("scatter-gather")
    implicit val materializer = ActorMaterializer()

    val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._

      val in = Source(1 to 10)
      val out = Sink.foreach(println)
      val bcast = builder.add(Broadcast[Int](2))
      val merge = builder.add(Merge[Int](2))

      val f1, f2, f3, f4 = Flow[Int].map(_ + 10)

      // Add 10 at each f stage
      in ~> f1 ~> bcast ~> f2 ~> merge ~> f3 ~> out
                  bcast ~> f4 ~> merge

      ClosedShape
    })

    g.run()


  }
}
