package countrydata

object StatsCalculator {

  // Counting functions for parsing statistics
  def countInvalid(countries: List[Country]): Int = {
    countries.filterNot(DataValidator.isValid).length
  }

  def countDuplicates(countries: List[Country]): Int = {
    val validEntries = countries.filter(DataValidator.isValid)
    validEntries.length - validEntries.map(_.code).distinct.length
  }

  def getValidCountries(countries: List[Country]): List[Country] = {
    countries.filter(DataValidator.isValid).distinctBy(_.code)
  }

  // Top 10 pays par population
  def topByPopulation(countries: List[Country], n: Int = 10): List[Country] = {
    countries.sortBy(-_.population).take(n)
  }
  // Top 10 pays par superficie
  def topByArea(countries: List[Country], n: Int = 10): List[Country] = {
    countries.sortBy(-_.area).take(n)
  }
  // Top 10 pays par PIB (filtrer ceux qui ont un PIB)
  def topByGdp(countries: List[Country], n: Int = 10): List[Country] = {
    countries.filter(_.gdp > 0).sortBy(-_.gdp).take(n)
  }
  // Compter pays par continent
  def countByContinent(countries: List[Country]): Map[String, Int] = {
    countries.groupBy(_.continent).map { case (continent, list) => (continent, list.length) }
  }
  // Population moyenne par continent
  def avgPopulationByContinent(countries: List[Country]): Map[String, Double] = {
    countries.groupBy(_.continent).map { case (continent, list) =>
      val avg = if (list.nonEmpty) list.map(_.population).sum.toDouble / list.length else 0.0
      (continent, avg)
    }
  }
  // Langues les plus répandues (top 5)
  def topLanguages(countries: List[Country], n: Int = 5): List[(String, Int)] = {
    countries
      .flatMap(_.languages)
      .groupBy(identity)
      .map { case (lang, occurrences) => (lang, occurrences.length) }
      .toList
      .sortBy(-_._2)
      .take(n)
  }
  // Pays multilingues (>= 3 langues officielles)
  def multilingualCountries(countries: List[Country], minLanguages: Int = 3): List[Country] = {
    countries.filter(_.languages.length >= minLanguages)
  }
  // Densité de population (population / area)
  def withDensity(countries: List[Country]): List[(Country, Double)] = {
    countries
      .filter(_.area > 0)
      .map(c => (c, c.population.toDouble / c.area))
  }
  // Top 10 pays les plus denses
  def topByDensity(countries: List[Country], n: Int = 10): List[(Country, Double)] = {
    withDensity(countries).sortBy(-_._2).take(n)
  }
  // PIB par habitant
  def withGdpPerCapita(countries: List[Country]): List[(Country, Double)] = {
    countries
      .filter(c => c.population > 0 && c.gdp > 0)
      .map(c => (c, c.gdp.toDouble / c.population))
  }
  // Top 10 pays les plus riches par habitant
  def topByGdpPerCapita(countries: List[Country], n: Int = 10): List[(Country, Double)] = {
    withGdpPerCapita(countries).sortBy(-_._2).take(n)
  }
}
