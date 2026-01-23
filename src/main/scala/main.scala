package countrydata

object Main extends App {

  println("Mini-ETL : Analyse de pays\n")

  val start = System.currentTimeMillis()

  // Load all countries (raw)
  val result = DataLoader.loadCountries("data/data_dirty.json")

  result match {
    case Right(allCountries) =>
      // Generate report (counting is done in StatsCalculator)
      val report = ReportGenerator.generateReport(allCountries)

      println(s"Total entries parsed: ${report.statistics.total_countries_parsed}")
      println(s"Invalid entries (parsing errors): ${report.statistics.parsing_errors}")
      println(s"Duplicates removed: ${report.statistics.duplicates_removed}")
      println(s"Valid countries: ${report.statistics.total_countries_valid}")
      val duration = (System.currentTimeMillis() - start) / 1000.0

      // Write both reports
      ReportGenerator.writeReports(report, duration, "output/results.json", "output/report.txt") match {
        case Right(_) => println("\nJSON report written")

        case Left(err) =>
          println(s"Error writing reports: $err")
      }
      println(f"\nTraitement effectué en $duration%.3f secondes")
    case Left(error) =>
      println(s"Error loading data: $error")
  }
}
