package utils

import utils.Validations.Validation

object RegexExtension {
  implicit class RichRegex(val underlying: Validation) extends AnyVal {
    def matches(s: String) = underlying.findFirstIn(s).nonEmpty
  }
}
