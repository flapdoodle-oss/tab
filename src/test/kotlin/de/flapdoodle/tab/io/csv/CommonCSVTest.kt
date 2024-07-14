package de.flapdoodle.tab.io.csv

import de.flapdoodle.tab.io.csv.CSV.with
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CommonCSVTest {
    @Test
    fun guessFormatOfSamples() {
        assertThat(with("homeassist-energie-export.csv") { reader ->
            CommonCSV.guessFormat(reader)
        }).isEqualTo(
            Format(
                separator = Format.COMMA,
                quote = Format.DOUBLE_QUOTE
            )
        )

        assertThat(with("pv-ertrag-2023.csv") { reader ->
            CommonCSV.guessFormat(reader)
        }).isEqualTo(
            Format(
                separator = Format.SEMICOLON,
                quote = Format.SINGLE_QUOTE
            )
        )

        assertThat(with("pv-ertrag-2023-variante2.csv") { reader ->
            CommonCSV.guessFormat(reader)
        }).isEqualTo(
            Format(
                separator = Format.SEMICOLON,
                quote = Format.SINGLE_QUOTE
            )
        )
    }
}