import org.scalatest._
import flatspec._
import matchers._
import models.{ProductReport, Statistic}
import rankings.RecursionRanker
import scala.collection.immutable.HashMap

class RecursionRankerSpec extends AnyFlatSpec with Common with should.Matchers  {

  it should "calculate statistics correctly for empty Rating" in {
    RecursionRanker.loop(Iterator.empty, Statistic.empty()) shouldEqual(Statistic.empty())
  }

  it should "calculate statistics correctly for one invalid Rating" in {
    RecursionRanker.loop(Iterator.single(""), Statistic.empty()) shouldEqual(Statistic(0, 1, HashMap.empty))
  }

  it should "calculate statistics correctly for one valid Rating" in {
    val productId = "test-01"
    val rating = 2
    RecursionRanker.loop(Iterator.single(s"test1,test,${productId},${rating}"), Statistic.empty()) shouldEqual(Statistic(1, 0, HashMap(productId -> ProductReport(rating, 1))))
  }

  it should "calculate statistics correctly for one valid and one invalid Rating" in {
    val productId = "test-01"
    val rating = 2

    val validLine = s"test1,test,${productId},${rating}"
    val invalidLine = ""
    RecursionRanker.loop(Iterator(validLine, invalidLine), Statistic.empty()) shouldEqual(Statistic(1, 1, HashMap(productId -> ProductReport(rating, 1))))
  }


  it should "calculate statistics correctly for multiple valid and multiple invalid Ratings" in {
    val ratings = Map(
      "test-01" -> Seq(2, 3, 5),
      "test-02" -> Seq(1, 3)
    )
    val invalidCount = 3

    val validLines: Seq[String] = ratings.flatMap { case (productId, productRatings) =>
      productRatings.map(r => s"test1,test,${productId},${r}")
    }.toSeq

    val invalidLines: Seq[String] = (0 until invalidCount).map(_ => "-")

    val expectedResult = Statistic(ratings.values.map(_.size).sum, invalidCount, ratings.mapValues(r => ProductReport(r.sum, r.size)))

    RecursionRanker.loop(Iterator((validLines ++ invalidLines): _*), Statistic.empty()) shouldEqual(expectedResult)
  }

  it should "calculate formatted statistics correctly" in {
    val actualResult = RecursionRanker.calculateStatistics(in)

    actualResult shouldEqual (expectedResult)
  }

}
