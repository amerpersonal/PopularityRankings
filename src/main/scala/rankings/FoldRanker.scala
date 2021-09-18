package rankings

import models.{FormattedStatistics, Rating, Statistics}
import rankings.RecursionRanker.loop

import scala.io.Source
import scala.util.{Failure, Success}

object FoldRanker extends Ranker {
  override def calculate(in: java.io.InputStream): String = ???

  def calculateStatistics(in: java.io.InputStream): FormattedStatistics = {
    val source = Source.fromInputStream(in).getLines()

    val start = System.currentTimeMillis()
    val s = source.foldLeft[Statistics](Statistics.empty())((acc, next) => {
      Rating(next) match {
        case Success(Rating(_, _, productId, rating)) => {
          val newProductRatings = rating :: acc.ratings.getOrElse(productId, List.empty[Int])

          val average = newProductRatings.sum.toDouble / newProductRatings.size.toDouble
          val newAverageMap = acc.averages.updated(productId, average)

          val newTotalMap = acc.numberOfRanks.updated(productId, newProductRatings.size)

          Statistics(acc.validLines + 1, acc.invalidLines, acc.ratings.updated(productId, newProductRatings), newAverageMap, newTotalMap)
        }
        case Failure(_) => acc.copy(invalidLines = acc.invalidLines + 1)
      }
    })

    println(s"calculation done in ${System.currentTimeMillis() - start} ms")

    FormattedStatistics(
      s.ratings.size,
      s.invalidLines,
      s.averageRatings().takeRight(3).map(_._1),
      s.averageRatings().take(3).map(_._1),
      s.productsByNumberOfRatings().lastOption.map(_._1),
      s.productsByNumberOfRatings().headOption.map(_._1)
    )

  }
}
