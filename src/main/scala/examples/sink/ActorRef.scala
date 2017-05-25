package examples.sink

import akka.NotUsed
import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object ActorRef {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("sink")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val source: Source[Int, NotUsed] = Source(1 to 3)
    val actor = system.actorOf(Props[MyActor])
    val last = source.runWith(Sink.actorRef(actor, onCompleteMessage = "done"))
  }

  class MyActor extends Actor {
    def receive = {
      case i: Int => println(i)
    }
  }
}
