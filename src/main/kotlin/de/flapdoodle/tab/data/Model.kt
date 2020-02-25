package de.flapdoodle.tab.data

import de.flapdoodle.tab.types.Id

data class Model(
    private val tables: Map<Id<out HasColumns>, HasColumns> = linkedMapOf()
) {

  fun add(tableDef: TableDef): Model {
    return copy(tables = tables + (tableDef.id() to tableDef))
  }

  fun add(calculatedTable: CalculatedTable): Model {
    return copy(tables = tables + (calculatedTable.id() to calculatedTable))
  }

  fun <T : HasColumns> table(id: Id<T>): T {
    @Suppress("UNCHECKED_CAST")
    val table = tables[id] as T?
    require(table != null) { "no table found for $id" }
    return table
  }

  fun tableIds(): Set<Id<out HasColumns>> {
    return tables.keys
  }

  fun tables(): Collection<HasColumns> {
    return tables.values
  }
}