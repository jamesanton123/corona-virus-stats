package com.jamesanton

import java.util.Date

import com.opencsv.CSVReader

import scala.io.Source

class CSSEGISandDataDownloader {
  def getStats(statType: StatType): (Seq[Date], Seq[CountryStat]) = {
    val reader = new CSVReader(Source.fromURL(statType.url).bufferedReader())
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
