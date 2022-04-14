package org.esgi.project.streaming.models

import play.api.libs.json.{Json, OFormat}

case class LikesWithTitle(
                          _id: String,
                          title: String,
                          score: Long
                         )
object LikesWithTitle {
  implicit val format: OFormat[LikesWithTitle] = Json.format[LikesWithTitle]
}