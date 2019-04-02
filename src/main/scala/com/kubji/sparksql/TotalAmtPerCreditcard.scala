package  com.kubji.sparksql

import org.apache.spark.sql.SparkSession


object TotalAmtPerCreditcard {

  def main(args: Array[String]) {

    val masterOfCluster = args(0)
    val inputPath = args(1)


    val sparkSession = SparkSession
      .builder()
      .master(masterOfCluster)
      .appName("Load csv data")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()


    val parseOptions = Map("header" -> "true", "inferSchema" -> "true")

    val transactionDf = sparkSession.read.format("csv").options(parseOptions).load(inputPath)

    val totalAmtPerCreditcard = transactionDf.select("cc_num", "amt")
    .groupBy("cc_num").sum("amt")
    .show(false)
  }
}