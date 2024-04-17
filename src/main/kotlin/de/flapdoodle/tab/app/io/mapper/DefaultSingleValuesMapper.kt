package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping
import de.flapdoodle.tab.app.io.file.FileSingleValue
import de.flapdoodle.tab.app.io.file.FileSingleValues
import de.flapdoodle.tab.app.model.data.SingleValue
import de.flapdoodle.tab.app.model.data.SingleValues

class DefaultSingleValuesMapper(
    private val singleValueMapper: Mapper<SingleValue<out Any>, FileSingleValue> = DefaultSingleValueMapper
) : Mapper<SingleValues, FileSingleValues> {
    override fun toFile(toFileMapping: ToFileMapping, src: SingleValues): FileSingleValues {
        return FileSingleValues(src.values.map { singleValueMapper.toFile(toFileMapping, it) })
    }

    override fun toModel(toModelMapping: ToModelMapping, src: FileSingleValues): SingleValues {
        return SingleValues(src.values.map { singleValueMapper.toModel(toModelMapping, it) })
    }
}