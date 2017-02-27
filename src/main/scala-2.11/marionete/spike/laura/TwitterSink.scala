package marionete.spike.laura

/**
  * Created by carlosrodrigues on 07/02/2017.
  */

import twitter4j._
import collection.JavaConversions._

/**
  * Oauth autentications - replace for properties file in the future
  */
object Util {
  val config = new twitter4j.conf.ConfigurationBuilder()
    .setOAuthConsumerKey("v81GD2JsaXdp6hAIDwzW5ckHk")
    .setOAuthConsumerSecret("utZUUSk76G3nF7j8BMgrJYHsRFOULBLqotJH8076jbVWKrfYR6")
    .setOAuthAccessToken("829353743011442688-RvwrwwGzmIaQNRUA5Ku8HPJCb6YIgDh")
    .setOAuthAccessTokenSecret("xPfu4i9YehYQg395VenBVwuyw0U7knY7qXByJhfgOys44")
    .build
}


/**
  * Twitter Reply Bot
  */
trait TwitterInstance {
  val twitter = new TwitterFactory(Util.config).getInstance
//  val twitter = new TwitterFactory().getInstance
}


class TwitterBot extends TwitterInstance {
  def updateProfileStatus(args: Array[String]) {
    val replyName = args(0)
    val replyText = args(1)
    val reply = "@" + replyName + " " + replyText
    twitter.updateStatus(new StatusUpdate(reply))
    //twitter.updateStatus(new StatusUpdate("Test"))
  }
}
