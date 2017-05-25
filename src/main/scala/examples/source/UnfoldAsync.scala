package examples.source

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

import scala.concurrent.Future

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object UnfoldAsync {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("source")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    // Fibonacci
    val source: Source[Int, NotUsed] = Source.unfoldAsync(0 → 1) {
      case (a, _) if a > 10000000 ⇒ Future(None)
      case (a, b) ⇒ Future(Some((b → (a + b)) → a))
    }
    source.runForeach(println)
  }
}
