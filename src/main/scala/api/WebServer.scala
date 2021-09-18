package api

import akka.actor.{ActorSystem, ClassicActorSystemProvider, Props}
import akka.http.scaladsl.Http
import akka.stream.Materializer
import akka.util.Timeout
import services.StatisticsActor

import scala.concurrent.duration._

object WebServer extends App with Api {
  val serverInterface = "0.0.0.0"
  val serverPort = 8080

  implicit val timeout  = Timeout(3.seconds)

  implicit val mat = Materializer.matFromSystem(new ClassicActorSystemProvider {
    override def classicSystem: ActorSystem = actorSystem
  })

  implicit lazy val actorSystem = ActorSystem("swipestox-activity-feed")

  val statisticsActor = actorSystem.actorOf(Props(new StatisticsActor()), "statistics_actor")

  println(s"Bounding HTTP server to ${serverInterface}: ${serverPort}")
  Http().newServerAt(serverInterface, serverPort).bind(routes)

}
