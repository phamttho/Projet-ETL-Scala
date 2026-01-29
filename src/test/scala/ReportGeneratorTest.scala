package countrydata

class ReportGeneratorTest extends munit.FunSuite {

  // Données de test
  val france = Country("France", "FR", "Paris", "Europe", 67000000L, 643801, 2716, List("French"), "EUR")
  val germany = Country("Germany", "DE", "Berlin", "Europe", 83000000L, 357386, 3846, List("German"), "EUR")
  val usa = Country("United States", "US", "Washington", "North America", 331000000L, 9833517, 21433, List("English"), "USD")
  val china = Country("China", "CN", "Beijing", "Asia", 1400000000L, 9596961, 14343, List("Mandarin"), "CNY")
  val switzerland = Country("Switzerland", "CH", "Bern", "Europe", 8600000L, 41285, 703, List("German", "French", "Italian", "Romansh"), "CHF")
  val invalidCountry = Country("Invalid", "XX", "", "", 0, 0, 0, List(), "")

  val testCountries = List(france, germany, usa, china, switzerland)

  // Tests generateReport
  test("generateReport crée un rapport avec les bonnes statistiques") {
    val report = ReportGenerator.generateReport(testCountries)

    assertEquals(report.statistics.total_countries_parsed, 5)
    assertEquals(report.statistics.total_countries_valid, 5)
    assertEquals(report.statistics.parsing_errors, 0)
    assertEquals(report.statistics.duplicates_removed, 0)
  }

  test("generateReport compte les erreurs de parsing") {
    val countries = testCountries :+ invalidCountry
    val report = ReportGenerator.generateReport(countries)

    assertEquals(report.statistics.total_countries_parsed, 6)
    assertEquals(report.statistics.parsing_errors, 1)
  }

  test("generateReport compte les doublons") {
    val duplicate = france.copy(name = "France 2")
    val countries = testCountries :+ duplicate
    val report = ReportGenerator.generateReport(countries)

    assertEquals(report.statistics.duplicates_removed, 1)
  }

  test("generateReport génère top 10 population") {
    val report = ReportGenerator.generateReport(testCountries)

    assertEquals(report.top_10_by_population.length, 5)
    assertEquals(report.top_10_by_population.head.name, "China")
    assertEquals(report.top_10_by_population.head.population, 1400000000L)
  }

  test("generateReport génère top 10 area") {
    val report = ReportGenerator.generateReport(testCountries)

    assertEquals(report.top_10_by_area.head.name, "United States")
  }

  test("generateReport génère top 10 GDP") {
    val report = ReportGenerator.generateReport(testCountries)

    assertEquals(report.top_10_by_gdp.head.name, "United States")
    assertEquals(report.top_10_by_gdp.head.gdp, 21433)
  }

  test("generateReport génère countries by continent") {
    val report = ReportGenerator.generateReport(testCountries)

    assertEquals(report.countries_by_continent("Europe"), 3)
    assertEquals(report.countries_by_continent("Asia"), 1)
  }

  test("generateReport génère top languages") {
    val report = ReportGenerator.generateReport(testCountries)

    assert(report.most_common_languages.nonEmpty)
    // German apparaît dans Germany et Switzerland
    assert(report.most_common_languages.exists(_.language == "German"))
  }

  test("generateReport génère multilingual countries") {
    val report = ReportGenerator.generateReport(testCountries)

    assertEquals(report.multilingual_countries.length, 1)
    assertEquals(report.multilingual_countries.head.name, "Switzerland")
  }

  test("generateReport génère top 10 density") {
    val report = ReportGenerator.generateReport(testCountries)

    assert(report.top_10_by_density.nonEmpty)
    // Germany est le plus dense
    assertEquals(report.top_10_by_density.head.name, "Germany")
  }
  test("generateReport génère top 10 GDP per capita") {
    val report = ReportGenerator.generateReport(testCountries)
    assert(report.top_10_by_gdp_per_capita.nonEmpty)
    // Switzerland devrait être en tête
    assertEquals(report.top_10_by_gdp_per_capita.head.name, "Switzerland")
  }

  test("generateReport génère pivot table des pays les plus riches") {
    val report = ReportGenerator.generateReport(testCountries)
    assert(report.pivot_richest_countries.nonEmpty)
    // Switzerland devrait être en tête
    assertEquals(report.pivot_richest_countries.head.name, "Switzerland")
    // Vérifier que toutes les colonnes sont présentes
    val firstCountry = report.pivot_richest_countries.head
    assert(firstCountry.population > 0)
    assert(firstCountry.gdp > 0)
    assert(firstCountry.gdp_per_capita > 0)
  }

  // Tests writeReports (avec fichiers temporaires)
  test("writeReports retourne Right si succès") {
    val report = ReportGenerator.generateReport(testCountries)
    val tempJson = java.nio.file.Files.createTempFile("test_report", ".json").toString
    val tempTxt = java.nio.file.Files.createTempFile("test_report", ".txt").toString

    val result = ReportGenerator.writeReports(report, 0.1, tempJson, tempTxt)

    assert(result.isRight)

    // Cleanup
    java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(tempJson))
    java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(tempTxt))
  }

  test("writeJsonReport retourne Left si chemin invalide") {
    val report = ReportGenerator.generateReport(testCountries)
    val invalidPath = "/invalid/path/that/does/not/exist/report.json"
    val result = ReportGenerator.writeJsonReport(report, invalidPath)
    assert(result.isLeft)
  }
}
