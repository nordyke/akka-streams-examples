package examples

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}
import akka.{Done, NotUsed}

import scala.concurrent.Future

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object BroadcastTweets {

  final case class Author(handle: String)

  final case class Hashtag(name: String)

  final case class Tweet(author: Author, timestamp: Long, body: String) {
    def hashtags: Set[Hashtag] =
      body.split(" ").collect { case t if t.startsWith("#") => Hashtag(t) }.toSet
  }

  val akkaTag = Hashtag("#akka")

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("reactive-tweets")
    implicit val materializer = ActorMaterializer()
    implicit val ec = system.dispatcher

    val tweets: Source[Tweet, NotUsed] = Source(List(
      Tweet(author = Author("Dave"), timestamp = 1, body = "blah blah #akka #second"),
      Tweet(author = Author("Bill"), timestamp = 1, body = "you dummy #akka #winning"),
      Tweet(author = Author("Teddy"), timestamp = 1, body = "you dummy #snack #not-akka")
    ))

    val writeAuthors: Sink[Any, Future[Done]] = Sink.foreach(println)
    val writeHashtags: Sink[Any, Future[Done]] = Sink.foreach(println)

    val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
      import akka.stream.scaladsl.GraphDSL.Implicits._

      val bcast = b.add(Broadcast[Tweet](2))
      tweets ~> bcast.in
      bcast.out(0) ~> Flow[Tweet].map(_.author) ~> writeAuthors
      bcast.out(1) ~> Flow[Tweet].mapConcat(_.hashtags.toList) ~> writeHashtags
      ClosedShape
    })

    g.run()


  }
}
