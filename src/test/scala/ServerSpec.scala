import java.io.{File, FileWriter}

import akka.actor.{ActorSystem, ClassicActorSystemProvider, Props}
import akka.http.scaladsl.model.{ContentTypes, MediaTypes, Multipart, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.Materializer
import akka.util.Timeout
import api.Api
import api.WebServer.actorSystem
import models.FormattedStatistics
import org.scalatest.matchers._
import org.scalatest.wordspec.AnyWordSpec
import services.StatisticsActor
import spray.json._

import scala.concurrent.duration._
import serializers.FormattedStatisticsSerializer._
import utils.Files

class ServerSpec extends AnyWordSpec with should.Matchers with ScalatestRouteTest with Api with Common {

  implicit val timeout = Timeout(3.seconds)

  implicit val mat = Materializer.matFromSystem(new ClassicActorSystemProvider {
    override def classicSystem: ActorSystem = actorSystem
  })

  implicit lazy val actorSystem = ActorSystem("swipestox-activity-feed")

  val statisticsActor = actorSystem.actorOf(Props(new StatisticsActor()), "statistics_actor")

  "return success on root GET request" in {
    Get() ~> rootRoute ~> check {
      responseAs[String] shouldEqual "success"
    }
  }

  "return correct schema for calculated statistics" in {
    val file = new File("rankings.csv")
    val formData = Multipart.FormData.fromFile("csv", MediaTypes.`multipart/form-data`, file, 100000)
    Post(s"/api/v1/statistics", formData) ~> routes ~> check {
      status shouldBe StatusCodes.OK

      val response = responseAs[String].parseJson

      response.asJsObject().fields.keySet shouldEqual FormattedStatistics.empty().toJson.asJsObject.fields.keySet
    }
  }

  "return correct calculated statistics for empty file" in {
    val file = Files.createFile("empty.csv").getOrElse(throw new Exception("Cannot create file"))

    val formData = Multipart.FormData.fromFile("csv", MediaTypes.`multipart/form-data`, file, 100000)
    Post(s"/api/v1/statistics", formData) ~> routes ~> check {
      status shouldBe StatusCodes.OK

      val response = responseAs[String].parseJson

      val expectedResponse = JsObject(
        ("bestRatedProducts", JsArray(Vector.empty)),
        ("worstRatedProducts", JsArray(Vector.empty)),
        ("validLines", JsNumber(0)),
        ("invalidLines", JsNumber(0)),
        ("mostRatedProduct", JsNull),
        ("lessRatedProduct", JsNull)
      )

      response.asJsObject() shouldEqual expectedResponse
    }
  }

}
