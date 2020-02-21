package de.flapdoodle.tab.data

import de.flapdoodle.tab.types.Id

data class Model(
    val tables: List<Table> = emptyList()
) {

  fun add(table: Table): Model {
    return copy(tables = tables + table)
  }

  fun changeTable(id: Id<Table>, change: (Table) -> Table): Model {
    return copy(tables = tables.map {
      when (it.id) {
        id -> change(it)
        else -> it
      }
    })
  }
}