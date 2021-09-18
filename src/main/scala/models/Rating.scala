package models

import utils.Validations

import scala.util.Try

case class Rating(buyerId: String, shopId: String, productId: String, rating: Int) {
  import utils.RegexExtension._

  require(
    Validations.AlphanumbericStartingWithLetter.matches(buyerId) &&
      Validations.AlphanumbericStartingWithLetter.matches(shopId) &&
      Validations.AlphanumbericEndingWithHypenAndLetter.matches(productId) &&
      rating >= 0 && rating <= 5
  )

  def >(p: Rating): Boolean = {
    rating > p.rating
  }

  def <(p: Rating): Boolean = {
    rating < p.rating
  }

  def isSameProduct(rating: Rating): Boolean = {
    productId == rating.productId
  }
}

object Rating {
  def apply(line: String): Try[Rating] = {
    Try {
      val Array(buyerId, shopId, productId, rating) = line.split(",").map(_.trim)

      Rating(buyerId, shopId, productId, rating.toInt)
    }
  }
}
