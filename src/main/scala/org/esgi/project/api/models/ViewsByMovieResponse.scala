package org.esgi.project.api.models

import org.esgi.project.api.models.StatsMovieResponse
import play.api.libs.json.{Json, OFormat}

case class ViewsByMovieResponse(
                         _id: Long,
                         title: String,
                         view_count : Long,
                         stats: StatsMovieResponse
                       ) {

  //  def increment(date) = this.copy(sum = this.sum + score, count = this.count + 1)
}
object ViewsByMovieResponse {
  implicit val format: OFormat[ViewsByMovieResponse] = Json.format[ViewsByMovieResponse]

  //  def empty: ViewsByMovie = ViewsByMovie(0,"",0,StatsMovie.empty)
}






