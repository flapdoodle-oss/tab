package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.NamedColumn

data class PersistableNamedColumn(
    val name: String,
    val columnId: PersistableColumnId
) {

  companion object : PersistableAdapter<NamedColumn<out Any>, PersistableNamedColumn> {
    override fun toPersistable(source: NamedColumn<out Any>): PersistableNamedColumn {
      return PersistableNamedColumn(
          name = source.name,
          columnId = PersistableColumnId.toPersistable(source.id)
      )
    }

    override fun from(context: FromPersistableContext, source: PersistableNamedColumn): NamedColumn<out Any> {
      return NamedColumn(
          name = source.name,
          id = PersistableColumnId.forType(source.columnId.type.type).from(context, source.columnId)
      )
    }
  }
}