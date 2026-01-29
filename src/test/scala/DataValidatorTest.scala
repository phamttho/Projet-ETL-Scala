package countrydata

class DataValidatorTest extends munit.FunSuite {

  // Données de test
  val validCountry = Country(
    name = "France",
    code = "FR",
    capital = "Paris",
    continent = "Europe",
    population = 67000000L,
    area = 643801,
    gdp = 2716,
    languages = List("French"),
    currency = "EUR"
  )
  val invalidCountryNoPopulation = validCountry.copy(population = 0)
  val invalidCountryNegativePopulation = validCountry.copy(population = -100)
  val invalidCountryNoArea = validCountry.copy(area = 0)
  val invalidCountryNoCode = validCountry.copy(code = "")
  val invalidCountryNoName = validCountry.copy(name = "")
  val invalidCountryNoCurrency = validCountry.copy(currency = "")

  // Tests isValid
  test("isValid retourne true pour un pays valide") {
    assert(DataValidator.isValid(validCountry))
  }

  test("isValid retourne false si population <= 0") {
    assert(!DataValidator.isValid(invalidCountryNoPopulation))
    assert(!DataValidator.isValid(invalidCountryNegativePopulation))
  }

  test("isValid retourne false si area <= 0") {
    assert(!DataValidator.isValid(invalidCountryNoArea))
  }

  test("isValid retourne false si code est vide") {
    assert(!DataValidator.isValid(invalidCountryNoCode))
  }

  test("isValid retourne false si name est vide") {
    assert(!DataValidator.isValid(invalidCountryNoName))
  }

  test("isValid retourne false si currency est vide") {
    assert(!DataValidator.isValid(invalidCountryNoCurrency))
  }

  // Tests filterValid
  test("filterValid filtre les pays invalides") {
    val countries = List(validCountry, invalidCountryNoPopulation, invalidCountryNoArea)
    val result = DataValidator.filterValid(countries)
    assertEquals(result.length, 1)
    assertEquals(result.head, validCountry)
  }

  test("filterValid supprime les doublons par code") {
    val duplicate = validCountry.copy(name = "France Duplicate")
    val countries = List(validCountry, duplicate)
    val result = DataValidator.filterValid(countries)
    assertEquals(result.length, 1)
    assertEquals(result.head.name, "France") // garde le premier
  }
}
