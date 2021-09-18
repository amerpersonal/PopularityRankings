package utils

import utils.Validations.Validation

import scala.util.matching.Regex

object RegexExtension {
  implicit class RichRegex(val underlying: Validation) extends AnyVal {
    def matches(s: String) = underlying.findFirstIn(s).nonEmpty
  }
}
