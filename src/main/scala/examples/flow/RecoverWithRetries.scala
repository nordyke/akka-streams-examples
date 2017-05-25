package examples.flow

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

import scala.util.control.NoStackTrace

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object RecoverWithRetries {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("flow")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val ex = new Exception("ex") with NoStackTrace

    Source(1 to 3)
      .map { a => if (a == 2) throw ex else a}
      .recoverWithRetries(3, { case t: Exception => Source(1 to 3)})
      .runForeach(println)

  }
}
