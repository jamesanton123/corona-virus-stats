package com.jamesanton

object App extends App{

  val downloader = new CSSEGISandDataDownloader
  val populationService = new PopulationService
  val countryNameMapper = Map(
    "US" -> "United States",
    "Italy" -> "Italy",
    "Germany" -> "Germany",
    "United Kingdom" -> "United Kingdom"
  )
  val segment = StatType.CONFIRMED.getUrlSegment
  val date = "comment this"
  //  val date = "0325"

  val url = s"https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/archived_data/" +
    s"archived_time_series/time_series_19-covid-${segment}_archived_${date}.csv"
  val stats = downloader.getStats(url)

  // Filter the stats list for only countries in the mapper
  val statsForCountriesInMapper = stats match {
    case (dates, countryStats) =>
      (dates, countryStats.filter(stat => countryNameMapper.contains(stat.countryRegion)))
  }

  // Aggregate the us stats (because its broken up by states)
  val groupedByCountryRegion = statsForCountriesInMapper match {
    case (dates, countryStats) => {
      val grouped = countryStats.groupBy(_.countryRegion)

      def sum(regionStats: Seq[CountryStat], name: String): CountryStat = {
        val aggregateValuesForCountry = Array.fill(dates.length)(0f)
        regionStats.foreach(countryStat=>{
          for (i <- aggregateValuesForCountry.indices) {
            aggregateValuesForCountry(i) += countryStat.values(i)
          }
        })
        CountryStat("", name, 0f, 0f, aggregateValuesForCountry)
      }

      val aggregates = grouped.map {
        case (name, regionStats) => sum(regionStats, name)
      }

      (dates, aggregates)
    }
  }

  // Divides by population
  val statsPerPopulation = groupedByCountryRegion match {
    case (dates, countryStats) =>
      (dates, countryStats.map {
        countryStat => {
          val population = getPopulationForCountry(countryStat.countryRegion)
          countryStat.copy(values = countryStat.values.map(value => value / population))
        }
      })
  }

  private def getPopulationForCountry(name: String): Int = {
    populationService.getPopulationForCountry(
      countryNameMapper.get(name).getOrElse(throw new IllegalArgumentException("Country name is not mapped"))
    )
  }

  statsPerPopulation match {
    case(dates, countryStats) => countryStats.foreach(countryStat => {
      val perPopulationAsAPercent = countryStat.values.last * 100
      println(f"As of ${dates.last} in ${countryStat.countryRegion}, " +
        f"$perPopulationAsAPercent%.10f percent, value was ${countryStat.values.last}")
    })
  }

 }

case class CountryStat(provinceState: String, countryRegion: String, lat: Float, long: Float, values: Seq[Float])





