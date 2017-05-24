package examples

import akka.{Done, NotUsed}
import akka.actor.{ActorSystem, Cancellable}
import akka.stream.scaladsl.{Flow, Source}
import akka.stream.{ActorMaterializer, IOResult, ThrottleMode}

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object ZipAndThrottle {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("zip-throttle")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val source: Source[Int, NotUsed] = Source(1 to 100)
    val factorials = source.scan(BigInt(1))((acc, next) => acc * next)

    val result: Future[Done] =
      factorials
        .zipWith(Source(0 to 100))((num, idx) => s"$idx! = $num")
        .throttle(1, 1.second, 1, ThrottleMode.shaping)
        .runForeach(println)

    result.onComplete(_ => system.terminate())
  }
}
