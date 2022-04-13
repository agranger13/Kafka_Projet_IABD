package org.esgi.project.api.models

import play.api.libs.json.{Json, OFormat}

case class StatWorstScoreResponse(
                       title: String,
                       views: Long
                     )

object StatWorstScoreResponse {
  implicit val format: OFormat[StatWorstScoreResponse] = Json.format[StatWorstScoreResponse]
}
