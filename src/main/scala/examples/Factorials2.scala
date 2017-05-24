package examples

import java.nio.file.Paths

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, IOResult}
import akka.util.ByteString

import scala.concurrent.Future

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object Factorials2 {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("factorials")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val source: Source[Int, NotUsed] = Source(1 to 100)
    val factorials = source.scan(BigInt(1))((acc, next) => acc * next)

    val result: Future[IOResult] =
      factorials.map(_.toString).runWith(lineSink("output/factorial2.txt"))



    result.onComplete(_ => system.terminate())
  }

  def lineSink(filename: String): Sink[String, Future[IOResult]] =
    Flow[String]
      .map(s => ByteString(s + "\n"))
      .toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)
}
