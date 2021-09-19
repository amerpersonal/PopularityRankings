package rankings

import models._
import scala.io.Source
import scala.util.{Failure, Success}

object FoldRanker extends Ranker {
  override def calculateStatistics(source: Iterator[String]): FormattedStatistics = {
    val statistic = source.foldLeft[Statistic](Statistic.empty)((acc, line) => {

      Rating(line) match {
        case Success(Rating(_, _, productId, rating)) => {

          val currentReport = acc.productReports.getOrElse(productId, ProductReport())
          val newReport = currentReport.copy(ratingSum = currentReport.ratingSum + rating, ratingCount = currentReport.ratingCount + 1)

          Statistic(acc.validLines + 1, acc.invalidLines, acc.productReports.updated(productId, newReport))
        }
        case Failure(_) => acc.copy(invalidLines = acc.invalidLines + 1)
      }
    })

    FormattedStatistics.fromStatistic(statistic)

  }
}
