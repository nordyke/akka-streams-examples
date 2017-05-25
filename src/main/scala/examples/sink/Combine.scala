package examples.sink

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Broadcast, Sink, Source}

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object Combine {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("sink")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val source: Source[Int, NotUsed] = Source(List(1, 2, 3))
    val sink = Sink.combine(Sink.head, Sink.last)(Broadcast[Int](_))
    source.runWith(sink)
  }
}
