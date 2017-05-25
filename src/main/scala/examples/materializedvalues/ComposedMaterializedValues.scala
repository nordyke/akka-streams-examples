package examples.materializedvalues

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Tcp.OutgoingConnection
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source, Tcp}
import akka.util.ByteString

import scala.concurrent.{Future, Promise}

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object ComposedMaterializedValues {


  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("composed-materialized-values")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    // Composed, nested Source
    val source: Source[Int, Promise[Option[Int]]] = Source.maybe[Int]
    val flow1: Flow[Int, Int, NotUsed] = Flow[Int].take(100)
    val nestedSource: Source[Int, Promise[Option[Int]]] =
      source.viaMat(flow1)(Keep.left).named("nestedSource")

    // Composed, nested Flow
    val flow2: Flow[Int, ByteString, NotUsed] = Flow[Int].map { i => ByteString(i.toString) }
    val flow3: Flow[ByteString, ByteString, Future[OutgoingConnection]] = Tcp().outgoingConnection("localhost", 8080)
    val nestedFlow: Flow[Int, ByteString, Future[OutgoingConnection]] = flow2.viaMat(flow3)(Keep.right).named("nestedFlow")

    // Composed, nested Sink
    val sink: Sink[ByteString, Future[String]] = Sink.fold("")(_ + _.utf8String)
    val nestedSink: Sink[Int, (Future[OutgoingConnection], Future[String])] = nestedFlow.toMat(sink)(Keep.both)

    // Custom class for pull materialized values
    case class MyClass(private val p: Promise[Option[Int]], conn: OutgoingConnection) {
      def close() = p.trySuccess(None)
    }

    def f(p: Promise[Option[Int]], rest: (Future[OutgoingConnection], Future[String])): Future[MyClass] = {
       val connFuture = rest._1
      connFuture.map(MyClass(p, _))
    }

    // Materializes to Future[MyClass]
    val runnableGraph: RunnableGraph[Future[MyClass]] = nestedSource.toMat(nestedSink)(f)

  }
}
