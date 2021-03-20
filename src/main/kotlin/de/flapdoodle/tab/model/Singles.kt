package de.flapdoodle.tab.model

class Singles(
    val id: Int = Table.idGenerator.nextIdFor(Singles::class),
    private val values: Map<ColumnId<Any>, Any> = emptyMap()
) {
}