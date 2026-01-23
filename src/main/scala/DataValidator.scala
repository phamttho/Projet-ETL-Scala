package countrydata

object DataValidator {

  def isValid(country: Country): Boolean = {
    country.population > 0 &&
      country.area > 0 &&
      country.code.trim.nonEmpty &&
      country.name.trim.nonEmpty &&
      country.currency.trim.nonEmpty
  }
  def filterValid(countries: List[Country]): List[Country] = {
    // 1. Filter valid countries
    val valid = countries.filter(isValid)
    println(s"Valid countries: ${valid.length}")

    // 2. Remove duplicates by country code (keep first)
    val unique = valid.distinctBy(_.code)
    println(s"After removing duplicates: ${unique.length}")
    unique
  }
}