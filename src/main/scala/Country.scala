
package countrydata

case class Country(
                    name: String,
                    code: String,
                    capital: String,
                    continent: String,
                    population: Long,
                    area: Int,
                    gdp: Int,
                    languages: List[String],
                    currency: String
                  )
// Country report
case class ParsingStatistics(
  total_countries_parsed: Int,
  total_countries_valid: Int,
  parsing_errors: Int,
  duplicates_removed: Int
)
case class TopCountryByPopulation(name: String, population: Long, continent: String)
case class TopCountryByArea(name: String, area: Int, continent: String)
case class TopCountryByGdp(name: String, gdp: Int, continent: String)
case class LanguageCount(language: String, count: Int)
case class MultilingualCountry(name: String, languages: List[String])

case class CountryReport(
  statistics: ParsingStatistics,
  top_10_by_population: List[TopCountryByPopulation],
  top_10_by_area: List[TopCountryByArea],
  top_10_by_gdp: List[TopCountryByGdp],
  countries_by_continent: Map[String, Int],
  average_population_by_continent: Map[String, Double],
  most_common_languages: List[LanguageCount],
  multilingual_countries: List[MultilingualCountry]
)
