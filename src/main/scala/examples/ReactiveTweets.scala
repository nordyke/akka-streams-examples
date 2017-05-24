package examples

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.{Done, NotUsed}

import scala.concurrent.Future

/**
  * Created by aaron.nordyke on 5/23/17.
  */
object ReactiveTweets {

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

    val authors: Source[Author, NotUsed] =
      tweets
        .filter(_.hashtags.contains(akkaTag))
        .map(_.author)

    val hashtags: Source[Hashtag, NotUsed] = tweets.mapConcat(_.hashtags.toList)

    val hashtagsDone: Future[Done] = hashtags.runForeach(println)
    val authorsDone: Future[Done] = authors.runForeach(println)

    val done = for {
      hashTagsResult <- hashtagsDone
      authorsResult <- authorsDone
    } yield (hashTagsResult, authorsResult)

    done.onComplete(_ => system.terminate())

  }
}
