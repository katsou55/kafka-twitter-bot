package marionete.spike.laura

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.lower

/**
  * Created by tomasfartaria on 15/02/2017.
  */
class QueryRestaurants(spark: SparkSession) extends Serializable {


  import spark.implicits._

  val restaurants = spark.read.parquet("./parquet/restaurants/*")

  val commentsYelp = spark.read.parquet("./parquet/commentsRating/*")

  val commentsTwitter = spark.read.parquet("./parquet/twitterCommentsRating/*")

  //val rootLogger = Logger.getRootLogger().setLevel(Level.ERROR)

  def getTopRest(queryCuisine: String) = {

    //  restaurantsMainFeatures.show(10)
    val restaurantsMainFeatures = queryCuisine match {
      case "" => restaurants.select($"name".as("name"),
        $"num_review".as("num_review"),
        $"rating".as("rating"))
      case _ => restaurants.select($"name".as("name"),
        $"num_review".as("num_review"),
        $"rating".as("rating"),
        lower($"cuisine").as("cuisine")).where($"cuisine" === queryCuisine)
    }

    val commentsYelpMainFeatures = commentsYelp.select($"restaurant".as("nameYelp"),
      $"post_rating".as("post_rating"))
//      commentsYelp.show(10)

    val commentsTwitterMainFeatures = commentsTwitter.select($"twitter_key".as("nameTwitter"),
      $"twitter_rating".as("twitter_rating"))
    //  commentsTwitterMainFeatures.show(10)

    val joinedData = restaurantsMainFeatures.join(commentsYelpMainFeatures, restaurantsMainFeatures("name") === commentsYelpMainFeatures("nameYelp"), "left_outer")
    //    joinedData.show(10)
    val joinedDataMainFeatures = joinedData.drop($"nameYelp")
      .select($"name", (($"num_review" * $"rating" + $"post_rating") / ($"num_review" + 1)).as("rating"),
        ($"num_review" + 1).as("num_review"))
//      joinedDataMainFeatures.show(10)
    val joinedData2 = joinedData.join(commentsTwitterMainFeatures, joinedData("name") === commentsTwitterMainFeatures("nameTwitter"), "left_outer")
    val joinedData3 = joinedData2.na.fill(0.0)
//      joinedData.show(10)
    val joinedData3MainFeatures = joinedData3.drop($"nameTwitter")
      .select($"name", (($"num_review" * $"rating" + $"twitter_rating") / ($"num_review" + 1)).as("rating"),
        ($"num_review" + 1).as("num_review")).orderBy($"rating".desc)
    //  joinedData2MainFeatures.orderBy($"rating".desc).show(10)
    joinedData3MainFeatures.select($"name").show()
    // only returns top 1 restaurant
    val result = joinedData3MainFeatures.select($"name").first().getString(0)
    result
  }

}
