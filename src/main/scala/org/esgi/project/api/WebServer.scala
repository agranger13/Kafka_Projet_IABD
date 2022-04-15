package org.esgi.project.api

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import org.apache.kafka.streams.{KafkaStreams, StoreQueryParameters}
import org.apache.kafka.streams.kstream.{KTable, Windowed}
import org.apache.kafka.streams.state.{QueryableStoreTypes, ReadOnlyKeyValueStore, ReadOnlyWindowStore, WindowStoreIterator}
import org.esgi.project.api.models.{MeanLatencyForURLResponse, MeanScorePerFilmResponse, MovieCountResponse, MovieStatResponse, StatsMovie, TitleViewsResponse, ViewsByMovie}
import org.esgi.project.streaming.StreamProcessing
import org.esgi.project.streaming.models.{InfoStatMovie, MeanScoreForFilm}

import java.time.Instant
import scala.jdk.CollectionConverters._

object WebServer extends PlayJsonSupport {

  def routes(streams: KafkaStreams): Route = {
    concat(
      path("movies" / Segment) { id: String =>
        get {
          val kvStore1min: ReadOnlyWindowStore[Long, InfoStatMovie] = streams
            .store(StreamProcessing.lastMinuteStoreName, QueryableStoreTypes.windowStore[Long,InfoStatMovie]())
          val toTime = Instant.now()
          val fromTime1 = toTime.minusSeconds(60)
          val fromTime5 = toTime.minusSeconds(300)

          val statsLastMinute = kvStore1min.fetch(id.toLong, fromTime1, toTime).asScala
            .foldLeft(MovieStatResponse.empty)(
              (agg,kv) => agg.cumul(kv.value.start_only,kv.value.half,kv.value.full)
            )

          val statsLast5Minutes = kvStore1min.fetch(id.toLong,fromTime5,toTime).asScala.toList
            .foldLeft(MovieStatResponse.empty)(
              (agg,kv) => agg.cumul(kv.value.start_only,kv.value.half,kv.value.full)
            )

          val allDatasFromBeginning = kvStore1min.all().asScala
            .filter(kv => kv.key.key() == id.toLong)
            .toList

          val statsFromBeginning = allDatasFromBeginning
            .foldLeft(MovieStatResponse.empty)(
              (agg,kv) => agg.cumul(kv.value.start_only,kv.value.half,kv.value.full)
            )

          val statsPerFilm = StatsMovie(statsFromBeginning,statsLastMinute,statsLast5Minutes)

          val title = allDatasFromBeginning.map(kv => kv.value.title).distinct

          complete(
            if(title.nonEmpty)
              ViewsByMovie(id.toLong,title.head,statsFromBeginning.start_only+statsFromBeginning.half + statsFromBeginning.full,statsPerFilm)
            else
              HttpResponse(StatusCodes.NotFound, entity = "Not found")

          )

        }
      },
      path("stats" / "ten"/ "best"/ "views") {
        get {
          val kvStoreMovieViews: ReadOnlyKeyValueStore[String, Long] = streams
            .store(StreamProcessing.TotalViewsPerFilmStoreName, QueryableStoreTypes.keyValueStore[String,Long]())

          complete(
            kvStoreMovieViews.all().asScala.map(kv => TitleViewsResponse(kv.key,kv.value)).toList.sortBy(- _.views).take(10)
          )
        }
      },
      path("stats"/ "ten"/ "best"/ "score"){
        get {
          val kvStoreMeanScorePerFilm: ReadOnlyKeyValueStore[String, MeanScoreForFilm] = streams
            .store(StreamProcessing.MeanScorePerFilmStoreName, QueryableStoreTypes.keyValueStore[String,MeanScoreForFilm]())

          complete(
            kvStoreMeanScorePerFilm.all().asScala.map(kv => MeanScorePerFilmResponse(kv.value.title,kv.value.meanScore))
              .toList.sortBy(- _.meanScore).take(10)
          )
        }
      },
      path("stats" / "ten"/ "worst"/ "views") {
        get {
          val kvStoreMovieViews: ReadOnlyKeyValueStore[String, Long] = streams
            .store(StreamProcessing.TotalViewsPerFilmStoreName, QueryableStoreTypes.keyValueStore[String,Long]())

          complete(
            kvStoreMovieViews.all().asScala.map(kv => TitleViewsResponse(kv.key,kv.value)).toList.sortBy(_.views).take(10)
          )
        }
      },
        path("stats"/ "ten"/ "worst"/ "score"){
        get {
          val kvStoreMeanScorePerFilm: ReadOnlyKeyValueStore[String, MeanScoreForFilm] = streams
            .store(StreamProcessing.MeanScorePerFilmStoreName, QueryableStoreTypes.keyValueStore[String,MeanScoreForFilm]())

          complete(
            kvStoreMeanScorePerFilm.all().asScala.map(kv => MeanScorePerFilmResponse(kv.value.title,kv.value.meanScore))
              .toList.sortBy(_.meanScore).take(10)
          )
        }
      }
    )
  }
}
