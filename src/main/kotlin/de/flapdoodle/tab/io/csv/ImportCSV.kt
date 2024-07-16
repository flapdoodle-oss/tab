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
                columnsOf(config)

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

    private fun columnsOf(config: ColumnConfig) {
        val indexType = indexTypeOf(config)
        if (indexType != null) {
            columnsOf(config, indexType)
        }
    }

    private fun columnsOf(config: ColumnConfig, indexType: TypeInfo<out Comparable<*>>) {
        // TODO HACK
//        val table = tableOf(indexType)
    }

    private fun <K: Comparable<K>> tableOf(indexType: TypeInfo<K>): Node.Table<K> {
        return Node.Table(
            name = Title("foo"),
            indexType = indexType
        )
    }

    private fun indexTypeOf(config: ColumnConfig): TypeInfo<out Comparable<*>>? {
        val indexConverter = requireNotNull(config.converter[config.indexColumn]) { "no converter for index ${config.indexColumn}" }
        val indexType = indexConverter.type
        if (TypeInfo.of(Comparable::class.java).isAssignable(indexType)) {
            return indexType as TypeInfo<Comparable<*>>
        }
        return null
    }

}