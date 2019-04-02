package com.kubji.sparksql.window

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._


object AmountSpentInPreviousSwipe {

  def main(args: Array[String]) {

    val masterOfCluster = args(0)
    val transactionPath = args(1)
    val customerPath = args(2)


    val sparkSession = SparkSession
      .builder()
      .master(masterOfCluster)
      .appName("Load csv data")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()

    import sparkSession.implicits._

    val parseOptions = Map("header" -> "true", "inferSchema" -> "true")

    val transactionDF = sparkSession.read.format("csv").options(parseOptions).load(transactionPath)

    val customerDF = sparkSession.read.format("csv").options(parseOptions).load(customerPath)

    val joinedDF = transactionDF.join(customerDF, "cc_num")
      .select("cc_num", "first", "last", "trans_time", "merchant", "amt")

    val prevAmtWindowSpec = Window.partitionBy('cc_num).orderBy('trans_time)

    val previousAmtSpentDF = joinedDF.withColumn("prevAmountSpent",
      lag(joinedDF("amt"), 1).over(prevAmtWindowSpec))


    previousAmtSpentDF.show(false)
  }
}