package org.esgi.project.api.models

import org.esgi.project.api.models.StatsMovie
import play.api.libs.json.{Json, OFormat}

case class ViewsByMovie(
                         _id: Long,
                         title: String,
                         view_count : Long,
                         stats: StatsMovie
                       ) {

  //  def increment(date) = this.copy(sum = this.sum + score, count = this.count + 1)
}
object ViewsByMovie {
  implicit val format: OFormat[ViewsByMovie] = Json.format[ViewsByMovie]

  //  def empty: ViewsByMovie = ViewsByMovie(0,"",0,StatsMovie.empty)
}






