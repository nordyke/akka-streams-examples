package examples.source

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

import scala.collection.immutable

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object ZipWithN {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("source")
    implicit val materializer = ActorMaterializer()

    val source = Source.zipWithN[Int, Int](_.head)(immutable.Seq(Source.single(1), Source.single(2)))
    source.runForeach(println)
  }
}
