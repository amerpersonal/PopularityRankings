package rankings

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import models.{FormattedStatistics, Rating, Statistics}
import spray.json._
import serializers.FormattedStatisticsSerializer._

import scala.annotation.tailrec
import scala.io.Source
import scala.util.{Failure, Success}

object RecursionRanker extends Ranker {

  @tailrec
  def loop(iterator: Iterator[String], acc: Statistics): Statistics = {
    if (iterator.hasNext) {
      Rating(iterator.next()) match {
        case Success(Rating(_, _, productId, rating)) => {

          val start = System.currentTimeMillis()

          val newProductRatings = rating :: acc.ratings.getOrElse(productId, List.empty[Int])
          val average = newProductRatings.sum.toDouble / newProductRatings.size.toDouble
          val newAverageMap = acc.averages.updated(productId, average)

          val newTotalMap = acc.numberOfRanks.updated(productId, newProductRatings.size)

          println(s"iteration takes ${System.currentTimeMillis() - start}")

          loop(iterator, Statistics(acc.validLines + 1, acc.invalidLines, acc.ratings.updated(productId, newProductRatings), newAverageMap, newTotalMap))
        }
        case Failure(_) => loop(iterator, acc.copy(invalidLines = acc.invalidLines + 1))
      }
    }
    else acc
  }


  def calculateStatistics(in: java.io.InputStream): FormattedStatistics = {
    val source = Source.fromInputStream(in).getLines()

    FormattedStatistics.fromStatistics(loop(source, Statistics.empty()))
  }

}
