package rankings

import models.{FormattedStatistics, Rating}
import scala.io.Source
import scala.util.{Success, Try}

/**
  * Calculating statistics for products using naive, primitive approach - iterating through scala collection multiple times
  */
object CollectionChainingRanker extends Ranker {

  override def calculateStatistics(source: Iterator[String]): FormattedStatistics = {
    val (valid, invalid) = source.map(Rating(_)).toIterable.toSeq.partition(_.isSuccess)
    val ratings = valid.map(_.get).groupBy(_.productId).mapValues(_.map(_.rating))

    val statsByAverage = ratings.toList.sortBy { case (product, ratings) =>
      ratings.sum.toDouble / ratings.size.toDouble
    }

    val statsByNumberOfRatings = ratings.toList.sortBy { case (product, ratings) =>
      ratings.size
    }

    FormattedStatistics(
      valid.size,
      invalid.size,
      statsByAverage.takeRight(3).reverse.map(_._1),
      statsByAverage.take(3).map(_._1),
      statsByNumberOfRatings.lastOption.map(_._1),
      statsByNumberOfRatings.headOption.map(_._1)
    )
  }


}
