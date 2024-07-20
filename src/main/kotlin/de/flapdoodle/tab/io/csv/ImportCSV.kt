package de.flapdoodle.tab.io.csv

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import de.flapdoodle.kfx.logging.Logging
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.ColumnId
import de.flapdoodle.tab.model.data.Columns
import java.io.Reader
import java.util.*


object ImportCSV {
    private val logger = Logging.logger(ImportCSV::class)

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

    fun <K : Comparable<K>> read(
        reader: Reader,
        config: ColumnConfig<K>,
        format: Format = Format()
    ): Node.Table<K> {
        val table = tableOf(config)
        val (indexColumn, indexConverter) = config.indexConverter

        return CSVReaderBuilder(reader)
            .withCSVParser(
                CSVParserBuilder()
                    .withErrorLocale(Locale.getDefault())
                    .withQuoteChar(format.quote)
                    .withSeparator(format.separator)
                    .build()
            ).build().use { csvReader ->
                val rows = csvReader.readAll()

                val headers = rows.subList(0, rows.size.coerceAtMost(config.headerRows))
                val columnsMap = columns(config, headerNamesByColumnIndex(headers))
                logger.debug { "columns: $columnsMap" }
                val columnToColumnIdMap = columnsMap.mapValues { (_, v) -> v.id }

                var tableWithColumns = table.copy(columns = Columns(columnsMap.values.toList()))
                val data = rows.subList(headers.size, rows.size)

                data.forEach { row ->
                    logger.debug { "row: ${row.toList()}" }

                    val indexValue =
                        requireNotNull(indexConverter.converter(row[indexColumn])) { "could not convert index" }

                    logger.debug { "index: $indexValue" }

                    config.converter.forEach { (columnIndex, converter) ->
                        val value = row[columnIndex]
                        logger.debug { "column[$columnIndex#${converter.type}]=$value" }
                        tableWithColumns = addValue(
                            tableWithColumns,
                            requireNotNull(columnToColumnIdMap[columnIndex]),
                            indexValue,
                            converter,
                            value
                        )
                    }
                }

                tableWithColumns
            }
    }

    private fun <K : Comparable<K>, V : Any> addValue(
        table: Node.Table<K>,
        columnId: ColumnId,
        key: K,
        converter: CsvConverter<V>,
        value: String?
    ): Node.Table<K> {
        val converted: V? = if (value != null) converter.converter.invoke(value) else null
        val typeInfo: TypeInfo<V> = converter.type
        return table.copy(columns = table.columns.add(columnId, key, typeInfo, converted))
    }

    private fun headerNamesByColumnIndex(headers: List<Array<String>>): Map<Int, Name> {
        var map = mapOf<Int, Name>()
        if (headers.isNotEmpty()) {
            map = headers[0].mapIndexed { index, s -> index to Name(s) }.toMap()
            headers.subList(1, headers.size).forEach { row ->
                row.forEachIndexed { index, s ->
                    val current = requireNotNull(map[index])
                    val changed = if (current.short == null) Name(
                        current.long + " - " + s,
                        current.long
                    ) else current.copy(long = current.long + " - " + s)
                    map = map + (index to changed)
                }
            }
        }
        return map
    }

    private fun <K : Comparable<K>> tableOf(config: ColumnConfig<K>): Node.Table<K> {
        return Node.Table(
            name = config.name,
            indexType = config.indexConverter.second.type
        )
    }

    private fun <K : Comparable<K>> columns(
        config: ColumnConfig<K>,
        headerMap: Map<Int, Name>
    ): Map<Int, Column<K, out Any>> {
        return config.converter.map {
            it.key to column<K>(
                config.indexConverter.second.type,
                it.value,
                requireNotNull(headerMap[it.key]) { "no entry for ${it.key}" })
        }.toMap()
    }

    private fun <K : Comparable<K>> column(
        indexType: TypeInfo<in K>,
        converter: CsvConverter<out Any>,
        name: Name
    ): Column<K, out Any> {
        return Column(
            name = name,
            indexType = indexType,
            valueType = converter.type
        )
    }

}