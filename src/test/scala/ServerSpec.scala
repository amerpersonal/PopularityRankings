import akka.actor.{ActorSystem, ClassicActorSystemProvider, Props}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.Materializer
import akka.util.Timeout
import api.Api
import api.WebServer.actorSystem
import org.scalatest.matchers._
import org.scalatest.wordspec.AnyWordSpec
import services.StatisticsActor

import scala.concurrent.duration._

class ServerSpec extends AnyWordSpec with should.Matchers with ScalatestRouteTest with Api {

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
}
