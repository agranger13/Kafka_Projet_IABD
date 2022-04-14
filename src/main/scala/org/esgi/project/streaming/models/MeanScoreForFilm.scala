package org.esgi.project.streaming.models

import play.api.libs.json.{Json, OFormat}

case class MeanScoreForFilm(
                             sum: Long,
                             count: Long,
                             meanLatency: Long
                           ) {
  def increment(latency: Long) = this.copy(sum = this.sum + latency, count = this.count + 1)

  def computeMeanLatency = this.copy(
    meanLatency = this.sum / this.count
  )
}

object MeanScoreForFilm {
  implicit val format: OFormat[MeanScoreForFilm] = Json.format[MeanScoreForFilm]

  def empty: MeanScoreForFilm = MeanScoreForFilm(0, 0, 0)
}

