package org.esgi.project.api.models

import play.api.libs.json.{Json, OFormat}

case class MovieCountResponse(
                               _id: Long,
                               title: String,
                               view_count : Long,
                               stats: statsMovie
                             )

case class statsMovie(
                       past: InfoStatMovie,
                       last_minute: InfoStatMovie,
                       last_five_minutes: InfoStatMovie
                     )

case class InfoStatMovie(
                          half: Long,
                          full : Long,
                          start_only: Long
                        )

object MovieCountResponse {
  implicit val format: OFormat[MovieCountResponse] = Json.format[MovieCountResponse]
}
