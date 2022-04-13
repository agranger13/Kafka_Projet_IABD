package org.esgi.project.api.models

import play.api.libs.json.{Json, OFormat}

case class StatWorstViewsResponse(
                       title: String,
                       views: Long
                     )

object StatWorstViewsResponse {
  implicit val format: OFormat[StatWorstViewsResponse] = Json.format[StatWorstViewsResponse]
}
