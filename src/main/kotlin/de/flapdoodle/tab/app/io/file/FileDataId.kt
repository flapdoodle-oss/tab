package de.flapdoodle.tab.app.io.file

sealed class FileDataId(
    open val id: Int,
) {
    data class SingleValueId(override val id: Int) : FileDataId(id)
    data class ColumnId(
        override val id: Int,
        val indexType: String
    ) : FileDataId(id)
}