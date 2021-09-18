import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import rankings.CollectionChainingRanker

class CollectionChainingRankerSpec extends AnyFlatSpec with Common with should.Matchers {
  it should "calculate formatted statistics correctly" in {
    val actualResult = CollectionChainingRanker.calculateStatistics(in)

    actualResult shouldEqual (expectedResult)
  }
}

