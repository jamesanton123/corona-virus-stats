package com.jamesanton

object App extends App{

    val downloader = new CSSEGISandDataDownloader
    val populationService = new PopulationService

    val (countryStatDates, countryStats) = downloader.getStats(StatType.CONFIRMED)

    val usStats = countryStats.filter(_.countryRegion.equalsIgnoreCase("us"))
    val usStatsValues: Seq[Seq[Float]] = usStats.map(_.values)
    val aggregateValuesUS = Array.fill(usStatsValues.head.length)(0f)
    usStatsValues.foreach(regionValues=>{
      for (i <- regionValues.indices) {
        aggregateValuesUS(i) = aggregateValuesUS(i) + regionValues(i)
      }
    })

    val countryNameMapper = Map(
      "US" -> "United States"
    )

    val usPopulation = populationService.getPopulationForCountry(countryNameMapper.get("US").getOrElse(throw new IllegalArgumentException("Country name is not mapped")))

    println(countryStatDates.zip(aggregateValuesUS).map{
      case (date, value) =>
        val perPopulationValue = value / usPopulation
        val perPopulationAsAPercent = perPopulationValue * 100
        f"As of $date, $perPopulationAsAPercent%.10f percent"
    }.last)
  }

case class CountryStat(provinceState: String, countryRegion: String, lat: Float, long: Float, values: Seq[Float])





