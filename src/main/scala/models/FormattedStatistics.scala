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



