package de.flapdoodle.tab.data

import de.flapdoodle.tab.types.Id

data class TableDef(
    val id: Id<TableDef> = Id.create(),
    private val columns: List<NamedColumn<out Any>> = listOf()
) : HasColumns {

  override fun id() = id
  override fun columns() = columns

  fun add(id: ColumnId<*>, name: String): TableDef {
    require(!columns.any { it.id == id }) { "column already added" }

    return copy(columns = columns + NamedColumn(name,id))
  }
}