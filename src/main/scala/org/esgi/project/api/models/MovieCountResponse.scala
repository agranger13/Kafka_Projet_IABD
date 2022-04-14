package org.esgi.project.api.models

import play.api.libs.json.{Json, OFormat}

case class MovieCountResponse(
                               _id: Long,
                               title: String,
                               view_count : Long
                             )

case class statsMovie(
                       past: InfoStatMovie,
                     )

case class InfoStatMovie(
                          half: Long,
                          full : Long
                        )

object MovieCountResponse {
  implicit val format: OFormat[MovieCountResponse] = Json.format[MovieCountResponse]
}
