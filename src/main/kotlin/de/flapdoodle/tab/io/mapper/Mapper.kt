package de.flapdoodle.tab.io.mapper

import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping

interface Mapper<T, F> {
    fun toFile(toFileMapping: ToFileMapping, src: T): F
    fun toModel(toModelMapping: ToModelMapping, src: F): T
}