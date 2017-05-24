package examples

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.{Done, NotUsed}

import scala.concurrent.Future

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object BasicIterator {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("QuickStart")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val source: Source[Int, NotUsed] = Source(1 to 100)

    val done: Future[Done] = source.runForeach(i => println(i))(materializer)

    done.onComplete(_ => system.terminate())
  }
}
