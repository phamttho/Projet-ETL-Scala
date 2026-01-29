package countrydata

import io.circe._
import io.circe.parser._
import scala.io.Source


object DataLoader {

  /*Reads a JSON file and parses countries with proper resource management */
  def loadCountries(filename: String): Either[String, List[Country]] = {
    try {
      val source = Source.fromFile(filename)
      val data = source.mkString
      source.close()

      parse(data).flatMap { json =>
        json.as[List[Json]].map { countries =>
          countries.map { countryJson =>
            val c = countryJson.hcursor
            Country(
              name = c.downField("name").as[String].getOrElse(""),
              code = c.downField("code").as[String].getOrElse(""),
              capital = c.downField("capital").as[String].getOrElse(""),
              continent = c.downField("continent").as[String].getOrElse(""),
              population = c.downField("population").as[Long].getOrElse(0L),
              area = c.downField("area").as[Int].getOrElse(0),
              gdp = c.downField("gdp").as[Int].getOrElse(0),
              languages = c.downField("languages").as[List[String]].getOrElse(List()),
              currency = c.downField("currency").as[String].getOrElse("")
            )
          }
        }
      }.left.map(e => s"Error parsing JSON: ${e.getMessage}")
    } catch {
      case e: Exception => Left(s"Error reading file: ${e.getMessage}")
    }
  }
  /* Loads countries from JSON file and filters for valid data */
  def loadValidCountries(filename: String): Either[String, List[Country]] = {
    loadCountries(filename).map { countries =>
      DataValidator.filterValid(countries)
    }
  }
}
