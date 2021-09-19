import java.io._

import models.Rating
import rankings._
import utils.{Files, Validations}

import scala.collection.immutable.{SortedMap, SortedSet, TreeMap}
import scala.io.Source
import scala.util.{Failure, Success, Try}
import scala.util.matching.Regex
import Files._

object Runner {




    //    def updateBestRated(statistics: Statistics, product: Rating): Statistics = {
    //      if (statistics.bestRatedProducts.size < 3) {
    //        statistics.copy(bestRatedProducts = (product :: statistics.bestRatedProducts).sorted)
    //      }
    //      else if (statistics.bestRatedProducts.last < product) {
    //
    //        statistics.copy(bestRatedProducts = statistics.bestRatedProducts.take(statistics.bestRatedProducts.size - 1) :+ product)
    //      }
    //      else statistics
    //    }
    //
    //    def updateWorstRated(statistics: Statistics, product: Rating): Statistics = {
    //      if (statistics.worstRatedProduct.size < 3) {
    //        statistics.copy(bestRatedProducts = (product :: statistics.worstRatedProduct).sorted.reverse)
    //      }
    //      else if (statistics.worstRatedProduct.last > product) {
    //
    //        statistics.copy(bestRatedProducts = statistics.bestRatedProducts.take(statistics.bestRatedProducts.size - 1) :+ product)
    //      }
    //      else statistics
    //    }



  def calculateWithTuple(in: java.io.InputStream): (Int, Map[String, Seq[Int]]) = {
    var br: BufferedReader = new BufferedReader(new InputStreamReader(in))


    //Iterator.continually(br.readLine()).takeWhile(null !=).foldLeft(Statistics.empty())((output, nextLine) => {

    Source.fromInputStream(in).getLines().foldLeft((0, Map.empty[String, List[Int]]))((output, nextLine) => {

      val nextProduct = Rating(nextLine)

      nextProduct match {
        case Success(Rating(_, _, productId, rating)) => {
          val currentRatings = output._2.getOrElse(productId, List.empty[Int])

          val newRat = rating :: currentRatings
          (output._1, output._2.updated(productId, newRat))
        }
        case Failure(_) => (output._1 + 1, output._2)
      }

    })

  }

//  def calculateWithStatistics(in: java.io.InputStream): Statistics = {
//    var br: BufferedReader = new BufferedReader(new InputStreamReader(in))
//
//    Source.fromInputStream(in).getLines().foldLeft(Statistics.empty())((output, nextLine) => {
//
//      val nextProduct = Rating(nextLine)
//
//      nextProduct match {
//        case Some(Rating(_, _, productId, rating)) => {
//          val currentRatings = output.ratings.getOrElse(productId, List.empty[Int])
//
//          val newRat = rating :: currentRatings
//          output.copy(ratings = output.ratings.updated(productId, newRat))
//        }
//        case None => output
//      }
//
//    })
//
//  }

  def loadRatings(in: java.io.InputStream): Seq[Try[Rating]] = {
    var br: BufferedReader = new BufferedReader(new InputStreamReader(in))

    Source.fromInputStream(in).getLines().map(Rating(_)).toIterable.toSeq
  }

  def calculateRecursion(in: java.io.InputStream): (Int, Map[String, List[Int]]) = {

    def loop(iterator: Iterator[String], acc: (Int, Map[String, List[Int]])): (Int, Map[String, List[Int]]) = {
      if (iterator.hasNext) {
        Rating(iterator.next()) match {
          case Success(Rating(_, _, productId, rating)) => {
            val productRatings = acc._2.getOrElse(productId, List.empty[Int])
            val newProductRatings = rating :: productRatings
            loop(iterator, (acc._1, acc._2.updated(productId, newProductRatings)))
          }
          case Failure(_) => loop(iterator, (acc._1 + 1, acc._2))
        }
      }
      else acc
    }

    loop(Source.fromInputStream(in).getLines(), (0, Map.empty))

  }


  case class ProductWithAverage(productId: String, average: Double) extends Product with Serializable {
    def equals(other: ProductWithAverage): Boolean = {
      productId == other.productId
    }
  }

  implicit val bestAverageProductOrdering = new Ordering[ProductWithAverage] {
    def compare(x: ProductWithAverage, y: ProductWithAverage): Int = {
      if (x.productId == y.productId) 1
      else {
        y.average.compareTo(x.average)
      }
    }
  }

//  def calculateRecursionAllTuples(in: java.io.InputStream): Statistic = {
//
//
//    def loop(iterator: Iterator[String], acc: Statistic): Statistic = {
//      if (iterator.hasNext) {
//        Rating(iterator.next()) match {
//          case Success(Rating(_, _, productId, rating)) => {
//            val productRatings = acc._2.getOrElse(productId, List.empty[Int])
//            val newProductRatings = rating :: productRatings
//
//            val average = newProductRatings.sum.toDouble / newProductRatings.size.toDouble
//
//            val newAverageMap = acc._3.updated(productId, average)
//            val newTotalMap = acc._4.updated(productId, newProductRatings.size)
//
//            loop(iterator, (acc._1, acc._2.updated(productId, newProductRatings), newAverageMap, newTotalMap))
//          }
//          case Failure(ex) => loop(iterator, (acc._1 + 1, acc._2, acc._3, acc._4))
//        }
//      }
//      else acc
//    }
//
//    loop(Source.fromInputStream(in).getLines(), Statistic.empty())
//
//  }




  def main1(args: Array[String]): Unit = {
    val alphanumbericStartingWithLetter = "^[a-zA-Z]+([a-zA-Z]|[0-9])*$".r

    val alphanumbericAndHypen = "^[a-zA-Z]+([a-zA-Z]|[0-9])*(-[0-9]+)+$".r


    val f = new File("rankings.csv")

    val io = new FileInputStream(f)

//    var start = System.currentTimeMillis()
//    val res1 = calculateWithStatistics(io)
//
//    println(s"Output calculated with statustics in ${System.currentTimeMillis() - start} ms : ${res1}")

    val io1 = new FileInputStream(f)

    var start = System.currentTimeMillis()
    val res2 = calculateWithTuple(io1)

    println(s"Output calculated with tuple in ${System.currentTimeMillis() - start} ms : ${res2}")


    val io2 = new FileInputStream(f)

    start = System.currentTimeMillis()

    val loaded = loadRatings(io2)

//    val ratings = valid.groupBy(_.get.productId).mapValues(_.map(_.get.rating))

    val ratings = loaded.collect { case Success(r) => r }.groupBy(_.productId).mapValues(_.map(_.rating))

    println(s"Output calculated by chaining ${System.currentTimeMillis() - start} ms : ${loaded.size - ratings.size} ratings: ${ratings}")

    val statsByAverage = ratings.toList.sortBy { case (product, ratings) =>
      ratings.sum.toDouble / ratings.size.toDouble
    }

    val top3 = statsByAverage.takeRight(3)

    val worst3 = statsByAverage.take(3)

    val statsByNumberOfRatings = ratings.toList.sortBy { case (product, ratings) =>
      ratings.size
    }

    println(s"Statistics by chaining ${System.currentTimeMillis() - start} ms")
    println(s"top3: ${top3}")
    println(s"worst3: ${worst3}")
    println(s"number of ratings first: ${statsByNumberOfRatings.last}")
    println(s"number of ratings last: ${statsByNumberOfRatings.head}")


    val io4 = new FileInputStream(f)

    start = System.currentTimeMillis()
    val res3 = calculateRecursion(io4)

    println(s"Recursion loaded in ${System.currentTimeMillis() - start} ms: ${res3}")

    val io14 = new FileInputStream(f)

    start = System.currentTimeMillis()
//    val res13 = calculateRecursionAllTuples(io14)
//
//    val rankings = res13._3.toList.sortBy(_._2)
//    val numberOfRatings = res13._4.toList.sortBy(_._2)
//
//    println(s"Recursion all tupple in ${System.currentTimeMillis() - start} ms")
//    println(s"top3 #${rankings.takeRight(3)}")
//    println(s"worst3 #${rankings.take(3)}")
//    println(s"most rated ${numberOfRatings.last}")
//    println(s"least rated ${numberOfRatings.head}")
//

    val io24 = new FileInputStream(f)

    start = System.currentTimeMillis()
//    val res23 = calculateRecursionAllClass(io24)
//
//    val rankings23 = res23.averages.toList.sortBy(_._2)
//    val numberOfRatings23 = res23.numberOfRanks.toList.sortBy(_._2)
//
//    println(s"Recursion all class in ${System.currentTimeMillis() - start} ms")
//    println(s"top3 #${rankings23.takeRight(3)}")
//    println(s"worst3 #${rankings23.take(3)}")
//    println(s"most rated ${numberOfRatings23.last}")
//    println(s"least rated ${numberOfRatings23.head}")


    val io5 = new FileInputStream(f)

    start = System.currentTimeMillis()
    val source = Source.fromInputStream(io5).getLines()

    println(s"Loaded file in ${System.currentTimeMillis() - start} ms: ${source.next()}")

    val io6 = new FileInputStream(f)
    val br = new BufferedReader(new InputStreamReader(io6))

    start = System.currentTimeMillis()
    val continually = Iterator.continually(br.readLine()).takeWhile(null !=)

    println(s"Loaded file continually in ${System.currentTimeMillis() - start} ms: ${continually.next()}")

  }

  def main(args: Array[String]): Unit = {
    for {
      filename <- args.headOption.orElse(throw new IOException("You must specify a file"))
      _ <- if (filename.split("\\.").last == "csv") Some(Unit) else throw new IOException("You must specify a valid CSV file")
      f <- Files.createFile(filename)
      in <- f.createInputStream()

      output = RecursionRanker.calculate(in)

      _ = in.close()
    } yield println(output)
  }
}
