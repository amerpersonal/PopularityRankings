package rankings

import models.{FormattedStatistics, Rating, Statistics}

import scala.io.Source
import scala.util.{Success, Try}

object CollectionChainingRanker extends Ranker {
  def loadRatings(in: java.io.InputStream): Seq[Try[Rating]] = {
    val source = Source.fromInputStream(in).getLines()

    if (source.isEmpty) Seq.empty else source.map(Rating(_)).toIterable.tail.toSeq
  }

  override def calculateStatistics(in: java.io.InputStream): FormattedStatistics = {
    val loaded = loadRatings(in)
    val (valid, invalid) = loaded.partition(_.isSuccess)
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
