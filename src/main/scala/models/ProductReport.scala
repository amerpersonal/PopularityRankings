package models

case class ProductReport(ratingSum: Int, ratingCount: Int)

object ProductReport {
  def apply(): ProductReport = ProductReport(0, 0)
}
