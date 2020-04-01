package gy.fraud

import com.datastax.spark.connector.cql.CassandraConnector
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._

object Detector {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder
      .master("local[3]")
      .appName("Fraud Detector")
      .config("spark.driver.memory", "2g")
      .config("spark.cassandra.connection.host", "localhost")
      .config("spark.sql.streaming.checkpointLocation", "checkpoint")
      .enableHiveSupport
      .getOrCreate()

    val schema = StructType(Array(
      StructField("unix_time", LongType, nullable = false),
      StructField("category_id", IntegerType, nullable = false),
      StructField("ip", StringType, nullable = true),
      StructField("type", StringType, nullable = true)
    ))

    import spark.implicits._
    import org.apache.spark.sql.functions._

    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "input")
      .option("startingOffsets", "earliest")
      .load()
      .select(col("key").cast(StringType), from_json(col("value").cast(StringType), schema).alias("data"))
      .drop("key")

    import org.apache.spark.sql.functions._
    val transformed = df
      .selectExpr("to_timestamp(data.unix_time) as unix_time", "data.category_id", "data.ip", "data.type")
      .withWatermark("unix_time", "30 seconds")
      .groupBy(
        window($"unix_time", "2 minutes", "1 minute"),
        $"ip",
        $"type"
      )
      .agg(count("*").as("count"))
      .filter("count > 10")
      .select($"ip", $"type", $"count")

    val connector = CassandraConnector(spark.sparkContext.getConf)
    val namespace = "fx"
    val table = "fraud"

    val sink = transformed
      .writeStream
      .queryName("KafkaToCassandraForeach")
      .outputMode("update")
      .foreach(new CassandraSinkForeach(connector, namespace, table))
      .start()

    sink.awaitTermination()
  }

}
