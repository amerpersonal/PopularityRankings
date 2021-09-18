package utils

import scala.util.matching.Regex

object Validations extends Enumeration {

  type Validation = Regex

  val AlphanumbericStartingWithLetter: Validation = "^[a-zA-Z]+([a-zA-Z]|[0-9])*$".r
  val AlphanumbericEndingWithHypenAndLetter: Validation = "^[a-zA-Z-]+([a-zA-Z]|[0-9])*(-[0-9]+)+$".r
}