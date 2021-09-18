package serializers

import models.FormattedStatistics
import spray.json.DefaultJsonProtocol
import spray.json._

object FormattedStatisticsSerializer extends DefaultJsonProtocol with NullOptions {
  implicit val formattedStatisticsSerializer = jsonFormat6(FormattedStatistics.apply)

}
