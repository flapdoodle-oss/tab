package de.flapdoodle.tab.io.csv

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.Title
import de.flapdoodle.tab.ui.views.calculations.Columns
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

    fun <K: Comparable<K>> read(
        reader: Reader,
        config: ColumnConfig<K>,
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
                columnsOf(config)

                val rows = csvReader.readAll()
                rows.forEachIndexed { index, row ->
                    if (index < config.headerRows) {
                        println("header: ${row.toList()}")
                    } else {
//                        println("row: ${row.toList()}")
                        val indexValue = config.indexConverter.second.converter(row[config.indexConverter.first])
                        print("$indexValue -> ")
                        
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

    private fun <K: Comparable<K>> columnsOf(config: ColumnConfig<K>): Node.Table<K> {
        return Node.Table(
            name = Title("foo"),
            indexType = config.indexConverter.second.type
        )
    }
}