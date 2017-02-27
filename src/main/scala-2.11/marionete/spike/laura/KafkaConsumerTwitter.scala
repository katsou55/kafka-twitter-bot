package marionete.spike.laura

import kafka.utils.Logging
import org.apache.spark.sql.SparkSession

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
  * Created by carlosrodrigues on 06/02/2017.
  */


/**
  * Kafka Consumer
  */
class KafkaConsumerTwitter extends Logging {

  import java.util.concurrent._
  import java.util.{Collections, Properties}

  import org.apache.kafka.clients.consumer._

  //import com.typesafe.config.ConfigFactory


  private val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("group.id", "Laura1")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
  props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100")
  props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000")

  //JSON or AVRO not working
  //props.put("key.deserializer", "org.apache.kafka.connect.json.JsonConverter")
  //props.put("value.deserializer", "org.apache.kafka.connect.json.JsonConverter")
  //props.put("schema.registry.url", "http://localhost:8081")

  //not working?
  props.put("auto.offset.reset", "latest")
  private val consumer = new KafkaConsumer[String, String](props)


  var executor: ExecutorService = null

  def shutdown() = {
    if (consumer != null)
      consumer.close()
    if (executor != null)
      executor.shutdown()
  }

  def run() = {

    val topic = "laura"
    consumer.subscribe(Collections.singletonList(topic))

    Executors.newSingleThreadExecutor.execute(new Runnable {
      override def run(): Unit = {

        val spark = SparkSession
          .builder()
          .appName("LAURA")
          .master("local[*]")
          .getOrCreate()

//        val lemma = new Lemmatizer()
//        val cuisinesMap: mutable.Map[String, Seq[String]] = lemma.cuisineLemma

        while (true) {
          val records = consumer.poll(1000)

          var previousUser = ""

          for (record <- records) {

            val status = new TwitterBot()
            /**
              * Here you perform your Action per record
              **/
            val user: String = get_userName(record.value())

            if (user != previousUser) {
              /**
                * Parsing the json with the tweet information
                */
              val text: String = get_text(record.value())
              val answer: String = get_answer(text).toLowerCase.replaceAll("[\\W]", " ")
              val answerLemma: Seq[String] = lemma.plainTextToLemmas(answer, lemma.stopWords, lemma.pipeline)
              val queryCuisine = cuisineChecker(answerLemma)

              println(queryCuisine)
              /**
                * Quering for a recommendation
                */
              val sparkQuery = new QueryRestaurants(spark)
              val topRestaurant = sparkQuery.getTopRest(queryCuisine: String)
              val finalAnswer = answer match {
                case "POSITIVE" => s"I feel that you're happy so I will recommend you the best $queryCuisine restaurant around: $topRestaurant"
                case "NEUTRAL" => s" yo yo enjoy a popular $queryCuisine place at: $topRestaurant"
                case "NEGATIVE" => s""""Feeling in the Dumps, don't be silly chumps" just try the best$queryCuisine restaurant in London: $topRestaurant"""
              }

              /**
                * Sent the twitter message
                */
              status.updateProfileStatus(Array(user, finalAnswer))
              //Testing actions below
              System.out.println("Received message: (" + user + " " + finalAnswer + ") at offset " + record.offset())
              previousUser = user
            }
          }
        }
      }
    })
  }

  val lemma = new Lemmatizer()
  val cuisinesMap: mutable.Map[String, Seq[String]] = lemma.cuisineLemma

  /**
    * method check if we can infer user cuisine preference
    **/
  def cuisineChecker(answerLemma: Seq[String]) = {
    val noCuisine = "No cuisines found"
    val ans = (for (ans <- answerLemma)
      yield {
        val a = cuisinesMap.find(_._2.contains(ans))
        a match {
          case Some(x) => x._1
          case None => noCuisine
        }
      }).filter(x => x != noCuisine) //.take(1).head

    val queryCuisine = if (ans.isEmpty) {
      ""
    }
    else ans.head

    queryCuisine
  }

  /**
    * method to parse a json and extract the text from a tweet
    **/
  def get_text(jsonString: String) = {
    import scala.util.parsing.json._

    val Some(M(json)) = JSON.parseFull(jsonString)
    val M(info) = json("payload")
    val S(text) = info("text")
    text
  }

  /**
    * method to parse a json and extract user name from a tweet
    **/
  def get_userName(jsonString: String) = {
    import scala.util.parsing.json._

    val Some(M(json)) = JSON.parseFull(jsonString)
    val M(info) = json("payload")
    val M(user) = info("user")
    val S(name) = user("screen_name")
    name
  }

  /**
    * method to get the recommendation as answer
    **/
  def get_answer(text: String) = {
    val sentiment = SentimentAnalyzer.mainSentiment(text)
    println(sentiment)
    sentiment.toString
  }

}

object KafkaConsumerTwitter extends App {

  val example = new KafkaConsumerTwitter()
  //example.get_answer("I am hungry")
  example.run()
}