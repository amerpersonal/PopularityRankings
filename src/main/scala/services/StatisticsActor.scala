package services

import java.io.InputStream

import akka.actor.Actor
import rankings.CollectionChainingRanker
import services.StatisticsActor.GetStatisticsRecursion

object StatisticsActor {
  case class GetStatisticsRecursion(in: InputStream)
}

class StatisticsActor extends Actor  {

  override def receive = {
    case GetStatisticsRecursion(in: InputStream) => {
      sender ! CollectionChainingRanker.calculate(in)
    }
  }

}
