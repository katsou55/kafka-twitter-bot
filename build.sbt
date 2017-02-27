name := "TwitterReplyBot"

version := "1.0"

scalaVersion := "2.11.8"

// https://mvnrepository.com/artifact/org.twitter4j/twitter4j-core
libraryDependencies += "org.twitter4j".%("twitter4j-core")% "4.0.6"

// https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients
libraryDependencies += "org.apache.kafka".%("kafka-clients")% "0.10.1.0"

// https://mvnrepository.com/artifact/org.apache.kafka/kafka_2.11
libraryDependencies += "org.apache.kafka".%("kafka_2.11")% "0.10.1.0"

// https://mvnrepository.com/artifact/com.typesafe/config
libraryDependencies += "com.typesafe".%("config")% "1.3.1"

// https://mvnrepository.com/artifact/org.scalatest/scalatest_2.11
libraryDependencies += "org.scalatest".%("scalatest_2.11")% "3.0.1"

// https://mvnrepository.com/artifact/org.apache.kafka/connect-json
libraryDependencies += "org.apache.kafka".%("connect-json")% "0.10.0.1"

libraryDependencies += "edu.stanford.nlp".%("stanford-corenlp")% "3.5.2" artifacts (Artifact("stanford-corenlp", "models"), Artifact("stanford-corenlp"))

// https://mvnrepository.com/artifact/org.apache.spark/spark-core_2.11
libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.1.0"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql_2.11
libraryDependencies += "org.apache.spark" % "spark-sql_2.11" % "2.1.0"

// https://mvnrepository.com/artifact/io.confluent.kafka/connect-assemblies
//libraryDependencies += "io.confluent.kafka".%("connect-assemblies")% "0.1.18"

// https://mvnrepository.com/artifact/io.confluent.kafka/connect-utils
//libraryDependencies += "io.confluent.kafka".%("connect-utils")% "0.1.18"
