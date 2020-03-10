package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.NamedColumn

data class PersistableNamedColumn(
    val name: String,
    val columnId: PersistableColumnId
) {

  companion object : ToPersistable<NamedColumn<out Any>, PersistableNamedColumn> {
    override fun toPersistable(source: NamedColumn<out Any>): PersistableNamedColumn {
      return PersistableNamedColumn(
          name = source.name,
          columnId = PersistableColumnId.toPersistable(source.id)
      )
    }
  }
}