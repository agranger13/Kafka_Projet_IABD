package org.esgi.project.streaming.models

import play.api.libs.json.{Json, OFormat}

import scala.::

case class Top10MostRated(
                         top10 : List[LikesWithTitle]
                         ){
  def add(newEntry: LikesWithTitle) = this.copy(top10 = this.top10 :+ newEntry)

  def computeTop10 = {
    if(top10.length > 10){
      this.copy(
        top10.sortWith(_.score > _.score).drop(10)
      )
    }
    this
  }
}


object Top10MostRated {
  implicit val format: OFormat[Top10MostRated] = Json.format[Top10MostRated]

  def empty: Top10MostRated = Top10MostRated(List())
}
