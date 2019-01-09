
name := "fraud-detector"

version := "0.1"

scalaVersion := "2.11.12"

sparkVersion := "2.4.0"

sparkComponents ++= Seq("sql", "streaming", "sql-kafka-0-10", "streaming-kafka-0-10")

spDependencies += s"datastax/spark-cassandra-connector:${sparkVersion.value}-s_2.11"

assemblyJarName in assembly := s"${name.value.replace(' ', '-')}-${version.value}.jar"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}