package examples.source

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object ActorRef {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("source")
    implicit val materializer = ActorMaterializer()

    val source: Source[Int, ActorRef] = Source.actorRef[Int](Int.MaxValue, OverflowStrategy.fail)
    val ref: ActorRef = source.toMat(Sink.foreach(println))(Keep.left).run()

    ref ! 1
    ref ! 2

  }

}
