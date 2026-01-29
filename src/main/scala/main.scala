package countrydata

package countrydata

object Main extends App {
  println("Mini-ETL : Analyse de pays\n")

  val start = System.currentTimeMillis()
  val filename = "data/data_dirty.json"

  val result = for {
    // 1 - Load data
    allCountries <- DataLoader.loadCountries(filename)
    // 2 - Valid data
    validCountries = DataValidator.filterValid(allCountries)
    // 3 - Stats (generate report)
    report = ReportGenerator.generateReport(allCountries)
    // 4 - Write reports
    duration = (System.currentTimeMillis() - start) / 1000.0
    _ <- ReportGenerator.writeReports(report, duration, "output/results.json", "output/report.txt")
  } yield (report, duration)

  result match {
    case Right((report, duration)) =>
      println(s"Total entries parsed: ${report.statistics.total_countries_parsed}")
      println(s"Invalid entries (parsing errors): ${report.statistics.parsing_errors}")
      println(s"Duplicates removed: ${report.statistics.duplicates_removed}")
      println(s"Valid countries: ${report.statistics.total_countries_valid}")
      println("\nJSON report written")
      println(f"\nTraitement effectué en $duration%.3f secondes")
    case Left(error) =>
      println(s"Error: $error")
  }
}



