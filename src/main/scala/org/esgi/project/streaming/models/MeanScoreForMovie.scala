package org.esgi.project.streaming.models

case class MeanScoreForMovie(
                              sum: Long,
                              count: Long,
                              meanScore: Long
                            ) {
  def increment(score: Long) = this.copy(sum = this.sum + score, count = this.count + 1)

  def computeMeanLatency = this.copy(
    meanScore = this.sum / this.count
  )
}

