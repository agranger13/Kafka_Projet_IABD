package org.esgi.project.api.models

import play.api.libs.json.{Json, OFormat}

case class StatBestScoreResponse(
                       title: String,
                       score: Long
                                )

object StatBestScoreResponse {
  implicit val format: OFormat[StatBestScoreResponse] = Json.format[StatBestScoreResponse]
}
