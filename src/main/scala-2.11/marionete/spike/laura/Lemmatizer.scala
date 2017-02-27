package marionete.spike.laura

/**
  * Created by carlosrodrigues on 16/02/2017.
  */

import edu.stanford.nlp.pipeline._
import edu.stanford.nlp.ling.CoreAnnotations._
import java.util.Properties

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

class Lemmatizer extends Serializable{

  def createNLPPipeline(): StanfordCoreNLP = {
    val props = new Properties()
    props.put("annotators", "tokenize, ssplit, pos, lemma")
    new StanfordCoreNLP(props)
  }

  def isOnlyLetters(str: String): Boolean = {
    str.forall(c => Character.isLetter(c))
  }

  def plainTextToLemmas(text: String, stopWords: Set[String],
                        pipeline: StanfordCoreNLP): Seq[String] = {
    val doc = new Annotation(text)
    pipeline.annotate(doc)

    val lemmas = new ArrayBuffer[String]()
    val sentences = doc.get(classOf[SentencesAnnotation])
    for (sentence <- sentences;
         token <- sentence.get(classOf[TokensAnnotation])) {
      val lemma = token.get(classOf[LemmaAnnotation])
      if (lemma.length > 2 && !stopWords.contains(lemma)
        && isOnlyLetters(lemma)) { 1
        lemmas += lemma.toLowerCase
      }
    }
    lemmas
  }

  val stopWords: Set[String] =
    """able,about,across,after,all,almost,also,am,among,an,
       ,do,does,either,else,ever,every,for,from,get,got,had,has,have,he,her,hers
  ,him,his,how,however,i,if,in,into,is,it,its,just,least,let,like,likely,may,
  me,might,most,must,my,neither,no,nor,not,of,off,often,on,only,or,other,our,
  own,rather,said,say,says,she,should,since,so,some,than,that,the,their,them,
  then,there,these,they,this,tis,to,too,twas,us,wants,was,we,were,what,when,
  where,which,while,who,whom,why,will,with,would,yet,you,your,a,b,c,d,e,f,g,h
  ,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,food""".split(",").toSet // food in the scenario is considerad a stop word

  val cuisines_list: Array[String] = Array("fondue", "bed & breakfast", "falafel", "mexican", "scandinavian",
                                          "turkish", "bakeries", "gluten-free", "gastropubs", "lounges", "ethiopian",
                                          "thai", "indian", "chinese","wine bars", "halal", "soul food", "modern european",
                                          "cantonese", "szechuan", "juice bars & smoothies", "taiwanese", "music venues",
                                          "street vendors", "bistros", "argentine", "polish", "british", "breakfast & brunch",
                                          "hawaiian", "peruvian", "japanese", "latin american", "diners", "fast food",
                                          "filipino", "cocktail bars", "spanish", "vietnamese", "pakistani", "burgers", "pizza",
                                          "portuguese", "burmese", "georgian", "coffee & tea", "austrian", "chicken shop", "italian",
                                          "caribbean", "imported food", "persian/iranian", "brasseries", "singaporean", "sushi bars",
                                          "tapas bars", "cafes", "sandwiches", "jazz & blues", "restaurants", "ramen", "vegetarian",
                                          "hot dogs", "laotian", "korean", "tea rooms", "french", "malaysian", "vegan", "basque",
                                          "food trucks", "barbeque", "american (traditional)", "middle eastern", "seafood", "dim sum",
                                          "fish & chips", "tapas/small plates", "brazilian", "greek", "steakhouses", "noodles", "champagne bars",
                                          "delis", "american (new)", "german", "asian fusion", "mediterranean", "pubs", "bars", "food stands",
                                          "karaoke", "afghan", "belgian", "delicatessen")
                                    .map(x => x.replaceAll("[\\W]", " "))

  val pipeline = createNLPPipeline()

  val cuisineLemma = {

    var cuisinesLemmas = scala.collection.mutable.Map[String, Seq[String]]()

    for(words <- cuisines_list) {
      val x = plainTextToLemmas(words,stopWords, pipeline)
      cuisinesLemmas += (words -> x)

    }
    cuisinesLemmas
  }


  //val answerLemma = Seq("brazilian","ola")

//  for(ans <- answerLemma) {
//    cuisineLemma.values.toSeq.contains(ans)
//    println()
//  }
//  val ans = for(ans <- answerLemma if cuisineLemma.find(_._2.contains(ans)).isInstanceOf[Option[Tuple2[String,Seq[String]]]]) yield {cuisineLemma.find(_._2.contains(ans)).getOrElse(("0",Seq("0")))._1
//  }
//  println(ans.filter(x=>x!="0").head)

}

