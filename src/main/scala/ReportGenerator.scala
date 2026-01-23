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

    CountryReport(statistics = statistics, top_10_by_population = top10Population, top_10_by_area = top10Area,
      top_10_by_gdp = top10Gdp, countries_by_continent = byContinent, average_population_by_continent = avgPopByContinent,
      most_common_languages = topLanguages, multilingual_countries = multilingual)}

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

  def writeTextReport(report: CountryReport, durationSeconds: Double, filename: String): Either[String, Unit] = {
    Try {
      val entriesPerSecond = if (durationSeconds > 0) report.statistics.total_countries_parsed / durationSeconds else 0.0

      val continentNames = Map("Africa" -> "Afrique", "Asia" -> "Asie", "Europe" -> "Europe", "North America" -> "Amérique du Nord",
        "South America" -> "Amérique du Sud", "Oceania" -> "Océanie")

      val sb = new StringBuilder
      sb.append("===============================================\n")
      sb.append("     RAPPORT D'ANALYSE - PAYS DU MONDE\n")
      sb.append("===============================================\n\n")

      sb.append("STATISTIQUES DE PARSING\n")
      sb.append("---------------------------\n")
      sb.append(f"- Entrées totales lues      : ${report.statistics.total_countries_parsed}%d\n")
      sb.append(f"- Entrées valides           : ${report.statistics.total_countries_valid}%d\n")
      sb.append(f"- Erreurs de parsing        : ${report.statistics.parsing_errors}%d\n")
      sb.append(f"- Doublons supprimés        : ${report.statistics.duplicates_removed}%d\n\n")

      sb.append("RÉPARTITION PAR CONTINENT\n")
      sb.append("-----------------------------\n")
      continentNames.foreach { case (eng, fr) =>
        val count = report.countries_by_continent.getOrElse(eng, 0)
        sb.append(f"- $fr%-23s : $count%d pays\n")
      }
      sb.append("\n")

      sb.append("TOP 10 - POPULATION\n")
      sb.append("----------------------\n")
      report.top_10_by_population.zipWithIndex.foreach { case (c, i) =>
        sb.append(f"${i + 1}%2d. ${c.name}%-22s : ${c.population}%,d hab.\n")
      }
      sb.append("\n")

      sb.append("TOP 10 - SUPERFICIE\n")
      sb.append("-----------------------\n")
      report.top_10_by_area.zipWithIndex.foreach { case (c, i) =>
        sb.append(f"${i + 1}%2d. ${c.name}%-22s : ${c.area}%,d km²\n")
      }
      sb.append("\n")

      sb.append("TOP 10 - PIB\n")
      sb.append("---------------\n")
      report.top_10_by_gdp.zipWithIndex.foreach { case (c, i) =>
        sb.append(f"${i + 1}%2d. ${c.name}%-22s : ${c.gdp}%,d milliards USD\n")
      }
      sb.append("\n")

      sb.append("LANGUES LES PLUS RÉPANDUES\n")
      sb.append("--------------------------------\n")
      report.most_common_languages.zipWithIndex.foreach { case (l, i) =>
        sb.append(f"${i + 1}%2d. ${l.language}%-22s : ${l.count}%d pays\n")
      }
      sb.append("\n")

      sb.append("MOYENNES PAR CONTINENT\n")
      sb.append("--------------------------\n")
      continentNames.foreach { case (eng, fr) =>
        val avg = report.average_population_by_continent.getOrElse(eng, 0.0)
        sb.append(f"- $fr%-23s : ${avg.toLong}%,d hab. (moyenne)\n")
      }
      sb.append("\n")

      sb.append("PAYS MULTILINGUES (>= 3 langues)\n")
      sb.append("------------------------------------\n")
      report.multilingual_countries.foreach { c =>
        sb.append(f"- ${c.name}%-22s : ${c.languages.mkString(", ")}\n")}
      sb.append("\n")

      sb.append("PERFORMANCE\n")
      sb.append("---------------\n")
      sb.append(f"- Temps de traitement       : $durationSeconds%.3f secondes\n")
      sb.append(f"- Entrées/seconde           : $entriesPerSecond%.0f\n\n")
      sb.append("===============================================\n")

      val path = Paths.get(filename)
      Files.write(path, sb.toString.getBytes(StandardCharsets.UTF_8))
    } match {
      case Success(_) => Right(())
      case Failure(exception) => Left(s"Error writing text report to $filename: ${exception.getMessage}")
    }
  }
  def writeReports(report: CountryReport, durationSeconds: Double, jsonFilename: String, textFilename: String): Either[String, Unit] = {
    for {
      _ <- writeJsonReport(report, jsonFilename)
      _ <- writeTextReport(report, durationSeconds, textFilename)
    } yield ()
  }
}
