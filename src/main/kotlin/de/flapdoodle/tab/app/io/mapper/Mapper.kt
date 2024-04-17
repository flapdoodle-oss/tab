package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping

interface Mapper<T, F> {
    fun toFile(toFileMapping: ToFileMapping, src: T): F
    fun toModel(toModelMapping: ToModelMapping, src: F): T
}