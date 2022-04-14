package org.esgi.project.streaming.models

import play.api.libs.json.{Json, OFormat}

case class Views(
                  _id: Long,
                  title: String,
                  adult: Boolean,
                  view_category: String
                )



object Views {
  implicit val format: OFormat[Views] = Json.format[Views]

  def empty: Views = Views(0, "", adult = false, view_category = "")
}
