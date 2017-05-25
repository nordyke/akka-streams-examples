package examples.source

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

import scala.collection.immutable

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object Repeat {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("source")
    implicit val materializer = ActorMaterializer()

    val source: Source[Int, NotUsed] = Source.repeat(1)

    source.runForeach(println)
  }
}
