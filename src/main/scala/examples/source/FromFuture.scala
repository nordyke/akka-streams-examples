package examples.source

import akka.NotUsed
import akka.actor.{ActorSystem, Cancellable}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object FromFuture {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("source")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val source: Source[Int, NotUsed] = Source.fromFuture(Future(1))
    source.runForeach(println)
  }
}
