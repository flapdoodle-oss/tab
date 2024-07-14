package de.flapdoodle.tab.io.csv

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import java.io.Reader
import java.util.*


object ImportCSV {

    fun read(
        reader: Reader,
        format: Format = Format()
    ): List<List<String>> {
        CSVReaderBuilder(reader)
            .withCSVParser(
                CSVParserBuilder()
                    .withErrorLocale(Locale.getDefault())
                    .withQuoteChar(format.quote)
                    .withSeparator(format.separator)
                    .build()
            )
            .build().use { csvReader ->
                return csvReader.readAll().map { it.toList() }
            };
    }


}