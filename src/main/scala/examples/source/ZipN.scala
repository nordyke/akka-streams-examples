package examples.source

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Merge, Source}

import scala.collection.immutable

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object ZipN {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("source")
    implicit val materializer = ActorMaterializer()

    val source = Source.zipN(immutable.Seq(Source.single(1), Source.single(2)))
    source.runForeach(println)
  }
}
