package examples.source

import java.util.concurrent.CompletableFuture

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

import scala.concurrent.Future

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object FromFutureSource {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("source")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val futureSource: Source[Int, NotUsed] = Source.fromFuture(Future(1))
    val source: Source[Int, Future[NotUsed]] = Source.fromFutureSource(Future(futureSource))
    source.runForeach(println)
  }
}
