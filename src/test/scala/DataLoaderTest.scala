package countrydata

class DataLoaderTest extends munit.FunSuite {

  // Tests loadCountries
  test("loadCountries retourne Right avec fichier valide") {
    // Utilise le fichier de données existant
    val result = DataLoader.loadCountries("data/data_clean.json")

    assert(result.isRight)
    result.foreach { countries =>
      assert(countries.nonEmpty)
    }
  }
  test("loadCountries retourne Left avec fichier inexistant") {
    val result = DataLoader.loadCountries("fichier_inexistant.json")

    assert(result.isLeft)
    result.left.foreach { error =>
      assert(error.contains("Error reading file"))
    }
  }

  // Tests loadValidCountries
  test("loadValidCountries filtre les données invalides") {
    val result = DataLoader.loadValidCountries("data/data_dirty.json")

    assert(result.isRight)
    result.foreach { countries =>
      // Tous les pays retournés doivent être valides
      countries.foreach { country =>
        assert(DataValidator.isValid(country), s"${country.name} devrait être valide")
      }
    }
  }

  test("loadValidCountries supprime les doublons") {
    val result = DataLoader.loadValidCountries("data/data_dirty.json")

    result.foreach { countries =>
      val codes = countries.map(_.code)
      assertEquals(codes.length, codes.distinct.length, "Il ne devrait pas y avoir de doublons")
    }
  }
  
}
