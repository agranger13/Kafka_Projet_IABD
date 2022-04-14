package org.esgi.project.streaming.models

import play.api.libs.json.{Json, OFormat}

case class ViewsCategories(start_only: Long,
                           half: Long,
                           full: Long = 0
                          )

object ViewsCategories {
  implicit val format: OFormat[ViewsCategories] = Json.format[ViewsCategories]
}




