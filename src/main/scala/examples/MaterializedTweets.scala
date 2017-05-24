package examples

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}
import akka.{Done, NotUsed}

import scala.concurrent.Future

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object MaterializedTweets {

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


    val count: Flow[Tweet, Int, NotUsed] = Flow[Tweet].map(_ => 1)

    val sumSink: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ + _)

    val counterGraph: RunnableGraph[Future[Int]] =
      tweets
        .via(count)
        .toMat(sumSink)(Keep.right)
    // short version:
    // val sum: Future[Int] = tweets.map(t => 1).runWith(sumSink)
    // runWith is a convenience method that returns the materialized value of the sink

    val sum: Future[Int] = counterGraph.run()

    sum.foreach(c => println(s"Total tweets processed: $c"))
    sum.onComplete(_ => system.terminate())

  }
}
