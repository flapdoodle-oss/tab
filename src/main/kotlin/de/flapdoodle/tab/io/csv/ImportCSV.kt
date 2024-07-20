package de.flapdoodle.tab.io.csv

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.Title
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.Columns
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
                val table = tableOf(config)

                val rows = csvReader.readAll()

                val headers = rows.subList(0, rows.size.coerceAtMost(config.headerRows))
                val headerMap = headerNamesByColumnIndex(headers)
                println("headers: $headerMap")

                val columnsMap = columns(config,headerMap)
                println("columns: $columnsMap")


                val data = rows.subList(headers.size, rows.size)

                data.forEachIndexed { index, row ->
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

    private fun headerNamesByColumnIndex(headers: List<Array<String>>): Map<Int, Name> {
        var map = mapOf<Int, Name>()
        if (headers.isNotEmpty()) {
            map = headers[0].mapIndexed { index, s -> index to Name(s) }.toMap()
            headers.subList(1, headers.size).forEach { row ->
                row.forEachIndexed { index, s ->
                    val current = requireNotNull(map[index])
                    val changed = if (current.short==null) Name(current.long + " - " + s, current.long) else current.copy(long = current.long + " - " +s)
                    map = map + (index to changed)
                }
            }
        }
        return map
    }

    private fun <K: Comparable<K>> tableOf(config: ColumnConfig<K>): Node.Table<K> {
        return Node.Table(
            name = config.name,
            indexType = config.indexConverter.second.type
        )
    }

    private fun <K: Comparable<K>> columns(config: ColumnConfig<K>, headerMap: Map<Int, Name>): Map<Int, Column<K, out Any>> {
        return config.converter.map { it.key to column<K>(config.indexConverter.second.type, it.value, requireNotNull(headerMap[it.key]) {"no entry for ${it.key}"}) }.toMap()
    }

    private fun <K: Comparable<K>> column(indexType: TypeInfo<in K>, converter: CsvConverter<out Any>, name: Name): Column<K, out Any> {
        return Column(
            name = name,
            indexType = indexType,
            valueType = converter.type
        )
    }

}