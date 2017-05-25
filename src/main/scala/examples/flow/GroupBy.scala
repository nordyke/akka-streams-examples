package examples.flow

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object GroupBy {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("flow")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val multi = Source(1 to 10)
      // split into 2 substreams
      .groupBy(2, i => i % 2 == 0)
      // immediately merge them back together
      .mergeSubstreams
      .runForeach(println)


  }
}

