package examples.source

import java.io.{BufferedReader, File, FileReader, FileWriter}

import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.ByteString

import scala.concurrent.{Future, Promise}

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object UnfoldResoureceAsync {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("source")
    implicit val materializer = ActorMaterializer()

    val manyLines = {
      ("a" * 100 + "\n") * 10 +
        ("b" * 100 + "\n") * 10 +
        ("c" * 100 + "\n") * 10 +
        ("d" * 100 + "\n") * 10 +
        ("e" * 100 + "\n") * 10 +
        ("f" * 100 + "\n") * 10
    }
    val manyLinesArray = manyLines.split("\n")

    val manyLinesFile = {
      val f = File.createTempFile("output/blocking-source-spec", ".tmp")
      new FileWriter(f).append(manyLines).close()
      f
    }

    val open: () ⇒ Future[BufferedReader] = () ⇒ Promise.successful(new BufferedReader(new FileReader(manyLinesFile))).future
    val read: (BufferedReader) ⇒ Future[Option[String]] = reader ⇒ Promise.successful(Option(reader.readLine())).future
    val close: (BufferedReader) ⇒ Future[Done] =
      reader ⇒ {
        reader.close()
        Promise.successful(Done).future
      }

    val source = Source.unfoldResourceAsync[String, BufferedReader](
      open,
      read,
      reader ⇒ Future.successful {
        Done
      })

    source.runForeach(println)
  }
}
