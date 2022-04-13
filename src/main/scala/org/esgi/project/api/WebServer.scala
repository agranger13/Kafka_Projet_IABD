package org.esgi.project.api

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import org.apache.kafka.streams.{KafkaStreams, StoreQueryParameters}
import org.apache.kafka.streams.kstream.{KTable, Windowed}
import org.apache.kafka.streams.state.{QueryableStoreTypes, ReadOnlyKeyValueStore, ReadOnlyWindowStore, WindowStoreIterator}
import org.esgi.project.api.models.{MeanLatencyForURLResponse, VisitCountResponse}
import org.esgi.project.streaming.StreamProcessing
import org.esgi.project.streaming.models.MeanLatencyForURL

import java.time.Instant
import scala.jdk.CollectionConverters._

/**
 * -------------------
 * Part.3 of exercise: Interactive Queries
 * -------------------
 */
object WebServer extends PlayJsonSupport {
  def routes(streams: KafkaStreams): Route = {
    concat(
      path("visits" / Segment) { period: String =>
        get {
          period match {
            case "30s" =>
              val kvStore30Seconds: ReadOnlyWindowStore[String, Long] = streams
                .store("visitsOfLast30Seconds", QueryableStoreTypes.windowStore[String,Long]())
              val toTime = Instant.now()
              val fromTime = toTime.minusSeconds(30)
              complete(
                //allKeyValues.groupBy(_._1).mapValues(_.map(_._2).sum).toList
                kvStore30Seconds.fetchAll(fromTime,toTime).asScala.map(kv => VisitCountResponse(kv.key.key(),kv.value)).toList
              )
            case "1m" =>
              val kvStore1Minute: ReadOnlyWindowStore[String, Long] = streams
                .store("visitsOfLast1Minute", QueryableStoreTypes.windowStore[String,Long]())
              val toTime = Instant.now()
              val fromTime = toTime.minusSeconds(60)
              complete(
                kvStore1Minute.fetchAll(fromTime,toTime).asScala.map(kv => VisitCountResponse(kv.key.key(),kv.value)).toList
              )
            case "5m" =>
              val kvStore5Minute: ReadOnlyWindowStore[String, Long] = streams
                .store("visitsOfLast5Minute", QueryableStoreTypes.windowStore[String,Long]())
              val toTime = Instant.now()
              val fromTime = toTime.minusSeconds(300)
              complete(
                kvStore5Minute.fetchAll(fromTime,toTime).asScala.map(kv => VisitCountResponse(kv.key.key(),kv.value)).toList
              )
            case _ =>
              // unhandled period asked
              complete(
                HttpResponse(StatusCodes.NotFound, entity = "Not found")
              )
          }
        }
      },
      path("latency" / "beginning") {
        get {
          val kvStoreMeanLatencyPerURL: ReadOnlyKeyValueStore[String, MeanLatencyForURL] = streams
            .store("meanLatencyPerUrl", QueryableStoreTypes.keyValueStore[String,MeanLatencyForURL]())

          complete(
            kvStoreMeanLatencyPerURL.all().asScala.map(kv => MeanLatencyForURLResponse(kv.key,kv.value.meanLatency)).toList
          )
        }
      }
    )
  }
}
