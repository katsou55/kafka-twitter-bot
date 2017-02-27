import marionete.spike.laura.Lemmatizer
import scala.collection.mutable



val lemma = new Lemmatizer()

val cuisinesMap: mutable.Map[String, Seq[String]] = lemma.cuisineLemma

val answer = "I real/ly like Tapas"

val answerLemma: Seq[String] = lemma
  .plainTextToLemmas(answer, lemma.stopWords, lemma.pipeline)

val noCusines = "No cuisines found"

val ans = (for (ans <- answerLemma)
  yield {
    val a = cuisinesMap.find(_._2.contains(ans))
    a match {
      case Some(x) => x._1
      case None => noCusines
    }
  }).filter(x => x != noCusines)

val queryCuisine = if (ans.isEmpty) {
  ""
}
else ans.head

 //Array("tapas/small plates").map(x => x.replaceAll("[\\W]", " "))



