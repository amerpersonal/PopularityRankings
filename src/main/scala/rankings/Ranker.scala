package rankings

import models.FormattedStatistics
import spray.json._
import serializers.FormattedStatisticsSerializer._

/**
  * base interface for calculating rating product statistics
  */
trait Ranker {
  /**
    *
    * @param in input stream of CSV file containing products with ratings
    * @return json representation of statistics in the following format
    *         {
                "bestRatedProducts": [
                    "product1",
                    "product2",
                    "product3"
                ],
                "invalidLines": 1,
                "lessRatedProduct": "product10",
                "mostRatedProduct": "product20",
                "validLines": 48,
                "worstRatedProducts": [
                    "product7",
                    "product8",
                    "product9"
                ]
            }
    */
  def calculate(in: java.io.InputStream): String = calculateStatistics(in).toJson.prettyPrint

  def calculateStatistics(in: java.io.InputStream): FormattedStatistics

  /** Calculates statistics while measuring execution time
    *
    * @param in input stream of CSV file containing products with ratings
    * @return tuple containing execution time in milliseconds and calculated statistics
    */
  def calculateAndMesaure(in: java.io.InputStream): (Long, FormattedStatistics) = {
    val start = System.currentTimeMillis()
    val output = calculateStatistics(in)

    (System.currentTimeMillis() - start, output)
  }
}