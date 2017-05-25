package examples.flow

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object ConflateWithSeed {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("flow")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    Source(1 to 5)
      .conflateWithSeed(seed = i ⇒ i)(aggregate = (sum, i) ⇒ sum + i)
      .map { i => Thread.sleep(1000); i }
      .runForeach(println)

  }
}
