package api

import java.io.InputStream
import java.util.concurrent.TimeUnit

import akka.actor.ActorRef
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives
import akka.stream.scaladsl.{Source, StreamConverters}
import akka.util.{ByteString, Timeout}
import services.StatisticsActor.GetStatisticsRecursion
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.Materializer

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

trait Api extends Directives {
  val apiPrefix = "api"
  val apiVersion = "v1"

  val statisticsActor: ActorRef
  implicit val timeout: Timeout
  implicit val mat: Materializer

  val statisticsRoute = post {
    path("statistics") {
      fileUpload("csv") { case (metadata, byteSource) =>
        val io = toInputStream(byteSource)

        val res: Future[String] = (statisticsActor ? GetStatisticsRecursion(io)).mapTo[String]

        onComplete(res) { statistics =>
          complete {
            //statistics
            HttpResponse(StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, statistics.get))

          }
        }
      }
    }

  }

  val rootRoute = get {
    path("") {
      complete {
        "success"
      }
    }
  }



  val routes = pathPrefix(apiPrefix / apiVersion) {
    rootRoute ~ statisticsRoute
  }


  def toInputStream (entity: Source[ByteString, Any] ): InputStream = {
    entity.runWith (StreamConverters.asInputStream (FiniteDuration (3, TimeUnit.SECONDS) ) )
  }
}
