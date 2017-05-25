package examples.sink

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object LastOption {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("sink")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val source: Source[Int, NotUsed] = Source(1 to 3)
    val last = source.toMat(Sink.lastOption)(Keep.right).run()
    last.onComplete(println)
  }
}
