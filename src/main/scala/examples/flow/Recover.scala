package examples.flow

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}

import scala.util.control.NoStackTrace

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object Recover {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("flow")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val ex = new Exception("ex") with NoStackTrace

    Source(1 to 3)
      .map { a => if (a == 2) throw ex else a}
      .recover { case _: Exception => 100000}
      .runForeach(println)

  }
}
