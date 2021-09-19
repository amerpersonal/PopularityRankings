import java.io.{File, FileInputStream}

import models.FormattedStatistics
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import utils.Files

import scala.io.Source

trait Common {
  val filename = "test.csv"

  val ratings = Map(
    "test-01" -> Seq(2, 3, 5),
    "test-02" -> Seq(1, 3)
  )
  val invalidCount = 3

  val validLines: Seq[String] = ratings.flatMap { case (productId, productRatings) =>
    productRatings.map(r => s"test1,test,${productId},${r}")
  }.toSeq

  val invalidLines: Seq[String] = (0 until invalidCount).map(_ => "-")


  val f = Files.createFileWithLines(filename, Seq("buyer id, shop, product id, rating") ++ validLines ++ invalidLines)

  val in = f.map(new FileInputStream(_)).getOrElse(throw new RuntimeException("No test file created"))

  val source = Source.fromInputStream(in).getLines()
  if (source.hasNext) source.next()

  val expectedResult = FormattedStatistics(ratings.values.map(_.size).sum, invalidCount, Seq("test-01", "test-02"), Seq("test-02", "test-01"), Some("test-01"), Some("test-02"))


//  after {
//    for {
//      file <- f
//      _ <- Files.removeFile(file)
//    } yield ()
//  }
}
