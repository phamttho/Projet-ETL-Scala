package countrydata

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.util.{Failure, Success, Try}

object ReportGenerator {

  def generateReport(allCountries: List[Country]): CountryReport = {
    val totalParsed = allCountries.length
    val parsingErrors = StatsCalculator.countInvalid(allCountries)
    val duplicatesRemoved = StatsCalculator.countDuplicates(allCountries)
    val validCountries = StatsCalculator.getValidCountries(allCountries)

    val statistics = ParsingStatistics(total_countries_parsed = totalParsed, total_countries_valid = validCountries.length,
      parsing_errors = parsingErrors, duplicates_removed = duplicatesRemoved)

    val top10Population = StatsCalculator.topByPopulation(validCountries)
      .map(c => TopCountryByPopulation(c.name, c.population, c.continent))
    val top10Area = StatsCalculator.topByArea(validCountries)
      .map(c => TopCountryByArea(c.name, c.area, c.continent))
    val top10Gdp = StatsCalculator.topByGdp(validCountries)
      .map(c => TopCountryByGdp(c.name, c.gdp, c.continent))
    val byContinent = StatsCalculator.countByContinent(validCountries)
    val avgPopByContinent = StatsCalculator.avgPopulationByContinent(validCountries)
    val topLanguages = StatsCalculator.topLanguages(validCountries)
      .map { case (lang, count) => LanguageCount(lang, count) }
    val multilingual = StatsCalculator.multilingualCountries(validCountries)
      .map(c => MultilingualCountry(c.name, c.languages))
    val top10Density = StatsCalculator.topByDensity(validCountries)
      .map { case (c, density) => DensityRanking(c.name, density, c.continent) }
    val top10GdpPerCapita = StatsCalculator.topByGdpPerCapita(validCountries)
      .map { case (c, gdpPc) => GdpPerCapitaRanking(c.name, gdpPc, c.continent) }

    // Pivot table: Top 10 richest countries with population, gdp, gdp per capita
    val pivotRichest = StatsCalculator.topByGdpPerCapita(validCountries)
      .map { case (c, gdpPc) => RichestCountryPivot(c.name, c.population, c.gdp, gdpPc) }

    CountryReport(statistics = statistics, top_10_by_population = top10Population, top_10_by_area = top10Area,
      top_10_by_gdp = top10Gdp, countries_by_continent = byContinent, average_population_by_continent = avgPopByContinent,
      most_common_languages = topLanguages, multilingual_countries = multilingual,
      top_10_by_density = top10Density, top_10_by_gdp_per_capita = top10GdpPerCapita,
      pivot_richest_countries = pivotRichest)}

//  Write Json report
  def writeJsonReport(report: CountryReport, filename: String): Either[String, Unit] = {
    Try {
      val json = report.asJson.spaces2
      val path = Paths.get(filename)
      Files.write(path, json.getBytes(StandardCharsets.UTF_8))
    } match {
      case Success(_) => Right(())
      case Failure(exception) => Left(s"Error writing JSON report to $filename: ${exception.getMessage}")
    }
  }
//Write report.txt
  def writeTextReport(report: CountryReport, durationSeconds: Double, filename: String): Either[String, Unit] = {
    Try {
      val entriesPerSecond = if (durationSeconds > 0) report.statistics.total_countries_parsed / durationSeconds else 0.0

      val continentNames = Map("Africa" -> "Afrique", "Asia" -> "Asie", "Europe" -> "Europe", "North America" -> "Amérique du Nord",
        "South America" -> "Amérique du Sud", "Oceania" -> "Océanie")

      val sb = new StringBuilder

      // Header
      sb.append(
        s"""===============================================
           |     RAPPORT D'ANALYSE - PAYS DU MONDE
           |===============================================
           |
           |STATISTIQUES DE PARSING
           |---------------------------
           |- Entrées totales lues      : ${report.statistics.total_countries_parsed}
           |- Entrées valides           : ${report.statistics.total_countries_valid}
           |- Erreurs de parsing        : ${report.statistics.parsing_errors}
           |- Doublons supprimés        : ${report.statistics.duplicates_removed}
           |
           |RÉPARTITION PAR CONTINENT
           |-----------------------------
           |""".stripMargin)

      // For-comprehension sur Map: itère sur chaque paire (clé anglaise, valeur française)
      // yield génère une nouvelle collection de strings formatées
      // mkString("\n") joint toutes les lignes avec des retours à la ligne
      val continentLines = (for ((eng, fr) <- continentNames) yield {
        val count = report.countries_by_continent.getOrElse(eng, 0)
        f"- $fr%-23s : $count%d pays"
      }).mkString("\n")
      sb.append(continentLines + "\n")

      sb.append(
        s"""
           |TOP 10 - POPULATION
           |----------------------
           |""".stripMargin)
      // For-comprehension avec zipWithIndex: associe chaque élément à son index (0, 1, 2...)
      // (c, i) déstructure le tuple (country, index)
      // yield transforme chaque pays en string formatée avec numérotation
      val populationLines = (for ((c, i) <- report.top_10_by_population.zipWithIndex) yield {
        f"${i + 1}%2d. ${c.name}%-22s : ${c.population}%,d hab."
      }).mkString("\n")
      sb.append(populationLines + "\n")

      sb.append(
        s"""
           |TOP 10 - SUPERFICIE
           |-----------------------
           |""".stripMargin)
      val areaLines = (for ((c, i) <- report.top_10_by_area.zipWithIndex) yield {
        f"${i + 1}%2d. ${c.name}%-22s : ${c.area}%,d km²"
      }).mkString("\n")
      sb.append(areaLines + "\n")

      sb.append(
        s"""
           |TOP 10 - PIB
           |---------------
           |""".stripMargin)
      val gdpLines = (for ((c, i) <- report.top_10_by_gdp.zipWithIndex) yield {
        f"${i + 1}%2d. ${c.name}%-22s : ${c.gdp}%,d milliards USD"
      }).mkString("\n")
      sb.append(gdpLines + "\n")

      sb.append(
        s"""
           |LANGUES LES PLUS RÉPANDUES
           |--------------------------------
           |""".stripMargin)
      val languageLines = (for ((l, i) <- report.most_common_languages.zipWithIndex) yield {
        f"${i + 1}%2d. ${l.language}%-22s : ${l.count}%d pays"
      }).mkString("\n")
      sb.append(languageLines + "\n")

      sb.append(
        s"""
           |MOYENNES PAR CONTINENT
           |--------------------------
           |""".stripMargin)
      val avgLines = (for ((eng, fr) <- continentNames) yield {
        val avg = report.average_population_by_continent.getOrElse(eng, 0.0)
        f"- $fr%-23s : ${avg.toLong}%,d hab. (moyenne)"
      }).mkString("\n")
      sb.append(avgLines + "\n")

      sb.append(
        s"""
           |PAYS MULTILINGUES (>= 3 langues)
           |------------------------------------
           |""".stripMargin)
      val multilingualLines = (for (c <- report.multilingual_countries) yield {
        f"- ${c.name}%-22s : ${c.languages.mkString(", ")}"
      }).mkString("\n")
      sb.append(multilingualLines + "\n")

      sb.append(
        s"""
           |TOP 10 - DENSITÉ (hab/km²)
           |---------------------------
           |""".stripMargin)
      val densityLines = (for ((c, i) <- report.top_10_by_density.zipWithIndex) yield {
        f"${i + 1}%2d. ${c.name}%-22s : ${c.density}%.2f hab/km²"
      }).mkString("\n")
      sb.append(densityLines + "\n")

      sb.append(
        s"""
           |TOP 10 - PIB PAR HABITANT
           |--------------------------
           |""".stripMargin)
      val gdpPerCapitaLines = (for ((c, i) <- report.top_10_by_gdp_per_capita.zipWithIndex) yield {
        f"${i + 1}%2d. ${c.name}%-22s : ${c.gdp_per_capita}%.2f USD/hab"
      }).mkString("\n")
      sb.append(gdpPerCapitaLines + "\n")

      // Pivot table: Top 10 richest countries
      sb.append(
        s"""
           |TABLEAU PIVOT - TOP 10 PAYS LES PLUS RICHES (PIB/hab)
           |-------------------------------------------------------------------------------
           |    Pays                  |     Population |   PIB (Mrd) |   PIB/hab (USD)
           |-------------------------------------------------------------------------------
           |""".stripMargin)
      val pivotLines = (for ((c, i) <- report.pivot_richest_countries.zipWithIndex) yield {
        f"${i + 1}%2d. ${c.name}%-20s | ${c.population}%,14d | ${c.gdp}%,11d | ${c.gdp_per_capita}%,15.2f"
      }).mkString("\n")
      sb.append(pivotLines + "\n")
      sb.append("-------------------------------------------------------------------------------\n")

      sb.append(f"""
           |PERFORMANCE
           |---------------
           |- Temps de traitement       : $durationSeconds%.3f secondes
           |- Entrées/seconde           : $entriesPerSecond%.0f
           |
           |===============================================
           |""".stripMargin)

      val path = Paths.get(filename)
      Files.write(path, sb.toString.getBytes(StandardCharsets.UTF_8))
    } match {
      case Success(_) => Right(())
      case Failure(exception) => Left(s"Error writing text report to $filename: ${exception.getMessage}")
    }
  }
  // For-comprehension sur Either: chaîne séquentiellement deux opérations qui peuvent échouer
  // Si writeJsonReport retourne Left(error), la chaîne s'arrête et retourne Left(error)
  // Si writeJsonReport retourne Right(_), on continue avec writeTextReport
  // _ <- signifie qu'on ignore la valeur Right(()) car on ne s'intéresse qu'au succès/échec
  // yield () retourne Right(()) si les deux opérations réussissent
  // Équivalent à: writeJsonReport(...).flatMap(_ => writeTextReport(...))
  def writeReports(report: CountryReport, durationSeconds: Double, jsonFilename: String, textFilename: String): Either[String, Unit] = {
    for {
      _ <- writeJsonReport(report, jsonFilename)
      _ <- writeTextReport(report, durationSeconds, textFilename)
    } yield ()
  }
}
