package gy.fraud

import org.apache.spark.sql.SparkSession

object Detector {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder
      .master("local[3]")
      .appName("Fraud Detector")
      .config("spark.driver.memory", "2g")
      .config("spark.cassandra.connection.host", "localhost")
      .enableHiveSupport
      .getOrCreate()

    val df = spark
      .read
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "input")
      .load()
      .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")

    df.show(numRows = 20, truncate = false)
  }

}
