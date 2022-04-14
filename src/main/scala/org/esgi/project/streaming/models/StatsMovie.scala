package org.esgi.project.streaming.models

import play.api.libs.json.{Json, OFormat}

case class StatsMovie(
                       past: InfoStatMovie,
                       last_minute: InfoStatMovie,
                       last_five_minutes: InfoStatMovie
                     )


object StatsMovie {
  implicit val format: OFormat[StatsMovie] = Json.format[StatsMovie]
  def empty: ViewsByMovie = ViewsByMovie(InfoStatMovie.empty, InfoStatMovie.empty, InfoStatMovie.empty)
}