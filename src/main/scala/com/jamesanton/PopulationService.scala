package com.jamesanton

import java.io.InputStreamReader
import com.opencsv.CSVReader

class PopulationService {
  // TODO: Find rest interface to get latest population data
  val countryPopulationsPerYearFName = "country-data.csv"

  def getPopulationForCountry(countryName: String): Int = {
    val populationsRaw = readPopulations()
    val countryPopulations = populationsRaw.takeRight(populationsRaw.length - 5)
    val countryPopulationData = countryPopulations.filter(_(0) == countryName).head
    val mostRecentPopulation = countryPopulationData.filter(_.nonEmpty).last.toInt
    mostRecentPopulation
  }

  private def readPopulations(): Array[Array[String]] = {
    val in = getClass.getClassLoader.getResourceAsStream(countryPopulationsPerYearFName)
    val reader = new CSVReader(new InputStreamReader(in))
    val populationDataRaw = reader.readAll().toArray()
    reader.close()
    in.close()
    populationDataRaw.map(_.asInstanceOf[Array[String]])
  }

}
