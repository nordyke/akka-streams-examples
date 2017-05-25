package examples.source

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Sink, Source, SourceQueueWithComplete}

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object Queue {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("source")
    implicit val materializer = ActorMaterializer()

    val source: Source[Int, SourceQueueWithComplete[Int]] = Source.queue[Int](Int.MaxValue, OverflowStrategy.backpressure)
    val queue = source.to(Sink.foreach(println)).run()

    queue.offer(1)
    queue.offer(2)
  }
}
