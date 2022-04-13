package org.esgi.project.api.models

import play.api.libs.json.{Json, OFormat}

case class StatBestViewsResponse(
                       title: String,
                       views: Long
                     )

object StatBestViewsResponse {
  implicit val format: OFormat[StatBestViewsResponse] = Json.format[StatBestViewsResponse]
}
