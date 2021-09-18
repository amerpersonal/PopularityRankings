package models

import spray.json.{DefaultJsonProtocol, JsArray, JsNull, JsNumber, JsObject, JsString, JsValue, RootJsonFormat}

import scala.collection.immutable.HashMap

case class FormattedStatistics(
                                validLines: Int,
                                invalidLines: Int,
                                bestRatedProducts: Seq[String],
                                worstRatedProducts: Seq[String],
                                mostRatedProduct: Option[String],
                                lessRatedProduct: Option[String]
                              )

object FormattedStatistics {
  def empty(): FormattedStatistics = FormattedStatistics(0, 0, Seq.empty, Seq.empty, None, None)

  def fromStatistics(s: Statistics): FormattedStatistics = {
    val averageRatings = s.averageRatings()
    val productsByNumberOfRankings = s.productsByNumberOfRatings()

    FormattedStatistics(
      s.validLines,
      s.invalidLines,
      averageRatings.takeRight(3).map(_._1),
      averageRatings.take(3).map(_._1),
      productsByNumberOfRankings.lastOption.map(_._1),
      productsByNumberOfRankings.headOption.map(_._1)
    )
  }

//  def fromTuple(s: Statistic): FormattedStatistics = {
//    val averageRatings = s._3.toList.sortBy(_._2)
//    val productsByNumberOfRankings = s._4.toList.sortBy(_._2)
//
//    FormattedStatistics(
//      s._2.size,
//      s._1,
//      averageRatings.takeRight(3).map(_._1),
//      averageRatings.take(3).map(_._1),
//      productsByNumberOfRankings.lastOption.map(_._1),
//      productsByNumberOfRankings.headOption.map(_._1)
//    )
//  }

  def fromStatistic(statistic: Statistic): FormattedStatistics = {
    val reports = statistic.productReports.toList
    val averages = reports.sortBy(p => p._2.ratingSum.toDouble / p._2.ratingCount.toDouble)
    val counts = reports.sortBy(p => p._2.ratingCount)

    FormattedStatistics(
      statistic.validLines,
      statistic.invalidLines,
      averages.takeRight(3).reverse.map(_._1),
      averages.take(3).map(_._1),
      counts.lastOption.map(_._1),
      counts.headOption.map(_._1)
    )
  }
}



