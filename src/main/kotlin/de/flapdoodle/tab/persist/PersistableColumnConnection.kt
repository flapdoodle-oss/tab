package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.nodes.ColumnConnection

data class PersistableColumnConnection(
    val id: PersistableColumnId,
    val type: Type
) {

  enum class Type {
    Values,
    Aggregates
  }

  companion object : PersistableAdapter<ColumnConnection<out Any>, PersistableColumnConnection> {
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

    override fun from(context: FromPersistableContext, source: PersistableColumnConnection): ColumnConnection<out Any> {
      return when (source.type) {
        Type.Values -> ColumnConnection.ColumnValues(PersistableColumnId.forType(Any::class).from(context, source.id))
        Type.Aggregates -> ColumnConnection.Aggregate(PersistableColumnId.forType(Any::class).from(context, source.id))
      }
    }
  }
}