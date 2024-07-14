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
            }
    }

    fun read(
        reader: Reader,
        config: ColumnConfig,
        format: Format = Format()
    ) {
        CSVReaderBuilder(reader)
            .withCSVParser(
                CSVParserBuilder()
                    .withErrorLocale(Locale.getDefault())
                    .withQuoteChar(format.quote)
                    .withSeparator(format.separator)
                    .build()
            )
            .build().use { csvReader ->
                val rows = csvReader.readAll()
                rows.forEachIndexed { index, row ->
                    if (index < config.headerRows) {
                        println("header: ${row.toList()}")
                    } else {
//                        println("row: ${row.toList()}")
                        config.converter.forEach { index, converter ->
                            val value = row[index]
//                            println("convert $value")
                            val converted = converter.converter(value)
//                            println("--> $converted")
                            print("$converted,")
                        }
                        println()
                    }
                }
            }
    }

}