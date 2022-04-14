package org.esgi.project.streaming

import io.github.azhur.kafkaserdeplayjson.PlayJsonSupport
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.kstream.{JoinWindows, Printed, TimeWindows, Windowed}
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream._
import org.esgi.project.streaming.models.{MeanLatencyForURL, Metric, Visit, VisitWithLatency}

import java.io.InputStream
import java.time.Duration
import java.util.Properties

object StreamProcessing extends PlayJsonSupport {

  import org.apache.kafka.streams.scala.ImplicitConversions._
  import org.apache.kafka.streams.scala.serialization.Serdes._

  val groupnumber: String = "groupe-5"

  val applicationName = s"web-events-stream-app-$groupnumber"
  val likesTopicName: String = "likes"
  val viewsTopicName: String = "views"

  val lastMinuteStoreName = "NumberViewsOfLast1Minute"
  val lastFiveMinutesStoreName = "NumberViewsOfLast5Minute"
  val fromBeginningStoreName = "NumberViewsFromBeginning"

  val Top10lowRatingStoreName = "Top10lowRating"
  val Top10highRatingStoreName = "Top10highRating"

  val props = buildProperties

  // defining processing graph
  val builder: StreamsBuilder = new StreamsBuilder

  // TODO: declared topic sources to be used
  val likes: KStream[String, Visit] = builder.stream[String, Visit](likesTopicName)
  val views: KStream[String, Metric] = builder.stream[String, Metric](viewsTopicName)

  // TODO Number of views per film
  class movie_key(var _id: String = "0", var view_category: String = "half")
  val viewsGroupedByMovie: KGroupedStream[String, Views] =  views
    .filter()
    .groupBy((key, value) => value._id)


  // TODO: implement a computation of the views (<10%, <90%, >90%) count per film for the last 30 seconds,
  // TODO: the last minute and the last 5 minutes
  val windows30: TimeWindows = TimeWindows.of(Duration.ofSeconds(30))
  val viewsOfLast30Seconds: KTable[Windowed[String], Long] =visitsGroupedByUrl.windowedBy(windows30)
    .count()(Materialized.as("visitsOfLast30Seconds"))

  val windows1: TimeWindows = TimeWindows.of(Duration.ofMinutes(1))
  val viewsOfLast1Minute: KTable[Windowed[String], Long] = visitsGroupedByUrl.windowedBy(windows1)
    .count()(Materialized.as("visitsOfLast1Minute"))

  val windows5: TimeWindows = TimeWindows.of(Duration.ofMinutes(5))
  val viewsOfLast5Minute: KTable[Windowed[String], Long] = visitsGroupedByUrl.windowedBy(windows5)
    .count()(Materialized.as("visitsOfLast5Minute"))

  // TODO:
  val likesWithViews: KStream[String, LikesWithTitle] = likes.join(views)(
    (likes:Likes, views:Views) => LikesWithTitle(likes._id, views.title, likes.score),
    JoinWindows.of(Duration.ofMinutes(2))
  )

  // TODO: based on the previous join, compute the mean latency per URL
  val meanLatencyPerUrl: KTable[String, MeanLatencyForURL] = likesWithViews.groupBy((_,value)=> value._id)
    .aggregate(Top10MostRated.empty)(
      (_,v, agg)=>{agg.increment(v.latency)}.computeMeanLatency,
    )(Materialized.as("meanLatencyPerUrl"))
  println(meanLatencyPerUrl.toStream.print(Printed.toSysOut()))
  // -------------------------------------------------------------
  // TODO: now that you're here, materialize all of those KTables
  // TODO: to stores to be able to query them in Webserver.scala
  // -------------------------------------------------------------

  def run(): KafkaStreams = {
    val streams: KafkaStreams = new KafkaStreams(builder.build(), props)
    streams.start()

    // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable() {
      override def run {
        streams.close
      }
    }))
    streams
  }

  // auto loader from properties file in project
  def buildProperties: Properties = {
    import org.apache.kafka.clients.consumer.ConsumerConfig
    import org.apache.kafka.streams.StreamsConfig
    val inputStream: InputStream = getClass.getClassLoader.getResourceAsStream("kafka.properties")

    val properties = new Properties()
    properties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationName)
    // Disable caching to print the aggregation value after each record
    properties.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0")
    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
    properties.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, "-1")
    properties.load(inputStream)
    properties
  }
}
