package examples.sink

import akka.NotUsed
import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object ActorRefWithAck {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("sink")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val source: Source[Int, NotUsed] = Source(1 to 3)
    val actor = system.actorOf(Props[MyActor])
    source.runWith(Sink.actorRefWithAck(
      ref = actor,
      onInitMessage = Init,
      ackMessage = Ack,
      onCompleteMessage = "done"
    ))
  }

  case object Init

  case object Ack

  class MyActor extends Actor {
    def receive = {
      case Init =>
        println("Init")
        sender() ! Ack
      case Ack => println("Ack")
      case i: Int =>
        println(i)
        sender() ! Ack
    }
  }

}
