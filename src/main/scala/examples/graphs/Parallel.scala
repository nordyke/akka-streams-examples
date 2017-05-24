package examples.graphs

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ClosedShape}

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object Parallel {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("parallel")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val topHeadSink = Sink.foreach(println)
    val bottomHeadSink = Sink.foreach(println)
    val sharedDoubler = Flow[Int].map(_ * 2)

    val g = RunnableGraph.fromGraph(GraphDSL.create(topHeadSink, bottomHeadSink)((_, _)) { implicit builder =>
      (topHS, bottomHS) =>
        import GraphDSL.Implicits._
        val broadcast = builder.add(Broadcast[Int](2))
        Source.single(1) ~> broadcast.in

        broadcast.out(0) ~> sharedDoubler ~> topHS.in
        broadcast.out(1) ~> sharedDoubler ~> bottomHS.in

        ClosedShape
    })

    val graph = g.run()


    val done = for {
      top <- graph._1
      bottom <- graph._2
    } yield (top, bottom)

    done.onComplete(_ => system.terminate)
  }
}
