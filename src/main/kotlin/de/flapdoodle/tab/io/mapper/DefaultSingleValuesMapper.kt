package de.flapdoodle.tab.io.mapper

import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileSingleValue
import de.flapdoodle.tab.io.file.FileSingleValues
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.model.data.SingleValues

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