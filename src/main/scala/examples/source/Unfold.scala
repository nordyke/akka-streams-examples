package examples.source

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object Unfold {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("source")
    implicit val materializer = ActorMaterializer()

    // Fibonacci
    val source: Source[Int, NotUsed] = Source.unfold(0 → 1) {
      case (a, _) if a > 10000000 ⇒ None
      case (a, b) ⇒ Some((b → (a + b)) → a)
    }
    source.runForeach(println)
  }
}
