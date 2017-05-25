package examples.source

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import java.util.concurrent.{CompletableFuture, CompletionStage}

import scala.concurrent.Future

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object FromCompletionStage {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("source")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val future = new CompletableFuture[Int]()
    val source: Source[Int, NotUsed] = Source.fromCompletionStage(future)
    source.runForeach(println)
  }
}
