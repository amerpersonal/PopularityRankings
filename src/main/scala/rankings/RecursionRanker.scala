package rankings

import models._
import scala.annotation.tailrec
import scala.io.Source
import scala.util.{Failure, Success}

object RecursionRanker extends Ranker {

  /** Recursively loops over iterator of product string representations without mutating data
    *
    * @param iterator - iterator of text, each element contains a string representation of product rating
    * @param acc - accumulating data structure, containing data relevant to aggregating products
    * @return - statistic data extracted from iterator
    */
  @tailrec
  def loop(iterator: Iterator[String], acc: Statistic): Statistic = {
    if (iterator.hasNext) {
      val line = iterator.next()

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

    if (source.hasNext) source.next()

    FormattedStatistics.fromStatistic(loop(source, Statistic.empty))
  }
}
