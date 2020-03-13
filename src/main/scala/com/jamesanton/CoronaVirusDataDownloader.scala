package com.jamesanton

import java.util.Date

import scala.io.Source
import com.opencsv.CSVReader

object CoronaVirusDataDownloader extends App {

  val confirmedUrl = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv"
  val deathsUrl    = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Deaths.csv"
  val recoveredUrl = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Recovered.csv"
  val confirmedStats = getStats(confirmedUrl)

  println(confirmedStats.filter(_.CountryRegion == "US"))
  
  def getStats(url: String): Seq[CountryStat] = {
    val reader = new CSVReader(Source.fromURL(url).bufferedReader())
    val rows = reader.readAll().toArray()
    reader.close()
    val dateFormat = new java.text.SimpleDateFormat("MM/dd/yy")
    val rawHeader = rows.head
    val countryStatRawRows = rows.tail
    val headerCells = rawHeader.asInstanceOf[Array[String]]
    val dateCells = headerCells.takeRight(headerCells.length - 4).map(dateFormat.parse(_)).toSeq
    val countryStats = countryStatRawRows.map(rawRow=>{
      val cells = rawRow.asInstanceOf[Array[String]]
      val valueCells = cells.takeRight(cells.length - 4).map(_.toInt).toSeq
      CountryStat(cells(0), cells(1), cells(2).toFloat, cells(3).toFloat, dateCells.zip(valueCells))
    })
    countryStats
  }

}

case class CountryStat(provinceState: String, CountryRegion: String, lat: Float, long: Float, values: Seq[(Date, Int)])
