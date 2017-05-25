package examples.source

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Source}

import scala.concurrent.Promise

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object Maybe {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("source")
    implicit val materializer = ActorMaterializer()

    val source: Source[Int, Promise[Option[Int]]] = Source.maybe
    source.runForeach(println)
  }
}
