package rankings

import models._
import spray.json._

import scala.annotation.tailrec
import scala.io.Source
import scala.util.{Failure, Success}
import serializers.FormattedStatisticsSerializer._

import scala.collection.immutable.HashMap

object SmartRecursionRanker extends Ranker {

  @tailrec
  def loop(iterator: Iterator[String], acc: Statistic): Statistic = {
    if (iterator.hasNext) {
      val line = iterator.next()

      println(s"iterating ${line}")
      Rating(line) match {
        case Success(Rating(_, _, productId, rating)) => {

          val currentReport = acc.productReports.getOrElse(productId, ProductReport())
          val newReport = currentReport.copy(ratingSum = currentReport.ratingSum + rating, ratingCount = currentReport.ratingCount + 1)

          loop(iterator, Statistic(acc.validLines + 1, acc.invalidLines, acc.productReports.updated(productId, newReport)))
        }
        case Failure(_) => loop(iterator, acc.copy(invalidLines = acc.invalidLines + 1))
      }
    }
    else acc
  }


  override def calculateStatistics(in: java.io.InputStream): FormattedStatistics = {
    val source = Source.fromInputStream(in).getLines()

    val columns = if (source.hasNext) source.next() else ""
    FormattedStatistics.fromStatistic(loop(source, Statistic.empty))
  }
}
