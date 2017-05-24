package examples.graphs

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.FanInShape.{Init, Name}
import akka.stream._
import akka.stream.scaladsl.{Balance, Flow, GraphDSL, Merge, MergePreferred, RunnableGraph, Sink, Source}

import scala.collection.immutable

/**
  * Created by aaron.nordyke on 5/23/17.
  */

/*
  ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┬──────────────────┐
                                                                                                        │PriorityWorkerPool│
  │                                                                                                     └──────────────────┘
┌─┐                                         ┌─────────────────┐         ┌─────────┐          ┌──────────────────┐          │
│ │────┐                                    │                 ├─┐     ┌─┤┌───────┐├─┐      ┌─┤                  │
└─┘    │                                    │                 │ │────▶│ ││Worker ││ │─────▶│ │                  │          │
  │    │                                    │                 ├─┘     └─┤└───────┘├─┘      └─┤                  │
       │                                    │                 │         └─────────┘          │                  │          │
  │    │                                    │                 │                              │                  │
       │                                    │                 │                              │                  │          │
  │    │                                    │                 │         ┌─────────┐          │                  │
       │      ┌───────────────────┐         │                 ├─┐     ┌─┤┌───────┐├─┐      ┌─┤                  │          │
  │    │    ┌─┤                   │         │                 │ │────▶│ ││Worker ││ │─────▶│ │                  │
       └───▶│ │                   │         │                 ├─┘     └─┤└───────┘├─┘      └─┤                  │          │
  │         └─┤ ┌───────────────┐ ├─┐     ┌─┤  ┌──────────┐   │         └─────────┘          │   ┌──────────┐   ├─┐        ┌─┐
              │ │MergePreferred │ │ │────▶│ │  │ Balance  │   │                              │   │  Merge   │   │ │───────▶│ │
  │         ┌─┤ └───────────────┘ ├─┘     └─┤  └──────────┘   │                              │   └──────────┘   ├─┘        └─┘
       ┌───▶│ │                   │         │                 │         ┌─────────┐          │                  │          │
  │    │    └─┤                   │         │                 ├─┐     ┌─┤┌───────┐├─┐      ┌─┤                  │
       │      └───────────────────┘         │                 │ │────▶│ ││Worker ││ │─────▶│ │                  │          │
  │    │                                    │                 ├─┘     └─┤└───────┘├─┘      └─┤                  │
       │                                    │                 │         └─────────┘          │                  │          │
  │    │                                    │                 │                              │                  │
       │                                    │                 │                              │                  │          │
  │    │                                    │                 │         ┌─────────┐          │                  │
┌─┐    │                                    │                 ├─┐     ┌─┤┌───────┐├─┐      ┌─┤                  │          │
│ │────┘                                    │                 │ │────▶│ ││Worker ││ │─────▶│ │                  │
└─┘                                         │                 ├─┘     └─┤└───────┘├─┘      └─┤                  │          │
  │                                         └─────────────────┘         └─────────┘          └──────────────────┘
                                                                                                                           │
  │
   ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘

Created with Monodraw

 */
object PriorityWorkerPool {

  def apply[In, Out](
                    worker: Flow[In, Out, Any],
                    workerCount: Int): Graph[PriorityWorkerPoolShape[In, Out], NotUsed] = {

    GraphDSL.create() { implicit b =>

      import GraphDSL.Implicits._

      val priorityMerge = b.add(MergePreferred[In](1))
      val balance = b.add(Balance[In](workerCount))
      val resultsMerge = b.add(Merge[Out](workerCount))

      priorityMerge ~> balance

      for (i <- 0 until workerCount)
        balance.out(i) ~> worker ~> resultsMerge.in(i)

      PriorityWorkerPoolShape(
        jobsIn = priorityMerge.in(0),
        priorityJobsIn = priorityMerge.preferred,
        resultsOut = resultsMerge.out
      )
    }
  }

  /*

    ┌───────┐         ┌───────────────────┐
    │       ├─┐     ┌─┤                   │
    │ 1-100 │ │────▶│  priority           │
    │       ├─┘     └─┤                   │
    └───────┘         │ ┌───────────────┐ ├─┐
                      │ │ priorityPool1 │ │ │──┐
    ┌───────┐         │ └───────────────┘ ├─┘  │
    │       ├─┐     ┌─┤                   │    │
    │ 1-100 │ │────▶│ │                   │    │
    │       ├─┘     └─┤                   │    │
    └───────┘         └───────────────────┘    │
                                               │
                      ┌───────────────────┐    │
                      │                   ├─┐  │
                      │                   │ │◀─┘
    ┌───────┐         │                   ├─┘
    │       │       ┌─┤ ┌───────────────┐ │
    │println│◀──────│ │ │ priorityPool2 │ │
    │       │       └─┤ └───────────────┘ │         ┌───────┐
    └───────┘         │                   ├─┐     ┌─┤       │
                      │           priority  │◀────│ │ 1-100 │
                      │                   ├─┘     └─┤       │
                      └───────────────────┘         └───────┘

    Created with Monodraw
   */
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("simplified-graph-api")
    implicit val materializer = ActorMaterializer()

    val worker1 = Flow[String].map("Worker 1: " + _)
    val worker2 = Flow[String].map("Worker 2: " + _)

    RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._

      val priorityPool1 = b.add(PriorityWorkerPool(worker1, 4))
      val priorityPool2 = b.add(PriorityWorkerPool(worker2, 2))

      Source(1 to 100).map("job: " + _) ~> priorityPool1.jobsIn
      Source(1 to 100).map("priority job: " + _) ~> priorityPool1.priorityJobsIn

      priorityPool1.resultsOut ~> priorityPool2.jobsIn
      Source(1 to 100).map("single-step, priority job: " + _) ~> priorityPool2.priorityJobsIn

      priorityPool2.resultsOut ~> Sink.foreach(println)
      ClosedShape
    }).run()

  }
}

case class PriorityWorkerPoolShape[In, Out](
                                             jobsIn: Inlet[In],
                                             priorityJobsIn: Inlet[In],
                                             resultsOut: Outlet[Out]) extends Shape {

  override val inlets: immutable.Seq[Inlet[_]] = jobsIn :: priorityJobsIn :: Nil
  override val outlets: immutable.Seq[Outlet[_]] = resultsOut :: Nil

  override def deepCopy(): Shape = PriorityWorkerPoolShape(
    jobsIn.carbonCopy(),
    priorityJobsIn.carbonCopy(),
    resultsOut.carbonCopy()
  )
}


// Alternate for above using Predefined Shape
class PriorityWorkerPoolShape2[In, Out](_init: Init[Out] = Name("PriorityWorkerPool"))
  extends FanInShape[Out](_init) {
  protected override def construct(i: Init[Out]) = new PriorityWorkerPoolShape2(i)

  val jobsIn = newInlet[In]("jobsIn")
  val priorityJobsIn = newInlet[In]("priorityJobsIn")
  // Outlet[Out] with name "out" is automatically created
}
