package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.nodes.ColumnConnection
import kotlin.reflect.KClass

data class PersistableColumnConnection(
    val id: PersistableColumnId,
    val type: Type
) {

  enum class Type {
    Values,
    Aggregates
  }

  companion object : ToPersistable<ColumnConnection<out Any>, PersistableColumnConnection> {
    override fun toPersistable(source: ColumnConnection<out Any>): PersistableColumnConnection {
      return when (source) {
        is ColumnConnection.ColumnValues<out Any> -> {
          PersistableColumnConnection(
              id = PersistableColumnId.toPersistable(source.columnId),
              type = Type.Values
          )
        }
        is ColumnConnection.Aggregate -> {
          PersistableColumnConnection(
              id = PersistableColumnId.toPersistable(source.columnId),
              type = Type.Aggregates
          )
        }
      }
    }

  }
}