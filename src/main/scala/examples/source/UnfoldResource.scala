package examples.source

import java.io.{BufferedReader, File, FileReader, FileWriter}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Merge, Source}

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object UnfoldResource {

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

    val source = Source.unfoldResource[String, BufferedReader](
      () ⇒ new BufferedReader(new FileReader(manyLinesFile)),
      reader ⇒ Option(reader.readLine()),
      reader ⇒ reader.close())

    source.runForeach(println)
  }
}
