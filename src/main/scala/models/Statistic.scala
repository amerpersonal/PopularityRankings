package models

import scala.collection.immutable.HashMap


case class Statistic(validLines: Int, invalidLines: Int, productReports: Map[String, ProductReport])

object Statistic {

  def empty(): Statistic = Statistic(0, 0, Map.empty)
}
