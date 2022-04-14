package org.esgi.project.api.models

import play.api.libs.json.{Json, OFormat}

case class StatsMovie(
                       past: MovieStatResponse,
                       last_minute: MovieStatResponse,
                       last_five_minutes: MovieStatResponse
                     )


object StatsMovie {
  implicit val format: OFormat[StatsMovie] = Json.format[StatsMovie]
  //  def empty: ViewsByMovie = ViewsByMovie(InfoStatMovie.empty, InfoStatMovie.empty, InfoStatMovie.empty)
}