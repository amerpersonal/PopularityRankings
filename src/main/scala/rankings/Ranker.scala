package rankings

import models.FormattedStatistics
import spray.json._
import serializers.FormattedStatisticsSerializer._

trait Ranker {
  def calculate(in: java.io.InputStream): String = calculateStatistics(in).toJson.prettyPrint

  def calculateStatistics(in: java.io.InputStream): FormattedStatistics

  def calculateAndMesaure(in: java.io.InputStream): (Long, String) = {
    val start = System.currentTimeMillis()
    val output = calculate(in)

    (System.currentTimeMillis() - start, output)
  }
}