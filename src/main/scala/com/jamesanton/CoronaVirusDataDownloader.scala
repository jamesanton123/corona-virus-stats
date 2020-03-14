package com.jamesanton

import java.util.Date

import scala.io.Source
import com.opencsv.CSVReader
object CoronaVirusDataDownloader extends App {

  val baseUrl = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/"
  val confirmedUrl = s"${baseUrl}time_series_19-covid-Confirmed.csv"
  val deathsUrl    = s"${baseUrl}time_series_19-covid-Deaths.csv"
  val recoveredUrl = s"${baseUrl}time_series_19-covid-Recovered.csv"

  val confirmedStats = getStats(confirmedUrl)
  val usStats = confirmedStats._2.filter(_.countryRegion.equalsIgnoreCase("us"))
  val dates = confirmedStats._1

  val usStatsValues: Seq[Seq[Float]] = usStats.map(_.values)
  val numValues = usStatsValues.head.length
  val aggregateValuesUS = Array.fill(numValues)(0f)

  usStatsValues.foreach(regionValues=>{
    for (i <- regionValues.indices) {
      aggregateValuesUS(i) = aggregateValuesUS(i) + regionValues(i)
    }
  })

  println(dates.zip(aggregateValuesUS).last)

  def getStats(url: String): (Seq[Date], Seq[CountryStat]) = {
    val reader = new CSVReader(Source.fromURL(url).bufferedReader())
    val rows = reader.readAll().toArray()
    reader.close()
    val dateFormat = new java.text.SimpleDateFormat("MM/dd/yy")
    val rawHeader = rows.head
    val countryStatRawRows = rows.tail
    val headerCells = rawHeader.asInstanceOf[Array[String]]
    val dateCells = headerCells.takeRight(headerCells.length - 5).map(dateFormat.parse(_)).toSeq
    val countryStats = countryStatRawRows.map(rawRow=>{
      val cells = rawRow.asInstanceOf[Array[String]]
      val valueCells = cells.filter(_.nonEmpty).takeRight(cells.length - 5).map(_.toFloat).toSeq
      CountryStat(cells(0), cells(1), cells(2).toFloat, cells(3).toFloat, valueCells)
    })
    (dateCells, countryStats)
  }

}

case class CountryStat(provinceState: String, countryRegion: String, lat: Float, long: Float, values: Seq[Float])
