package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.ColumnId
import java.math.BigDecimal
import kotlin.reflect.KClass

data class PersistableColumnId(
    val id: Int,
    val type: VariableOrColumnType
) {

  companion object : ToPersistable<ColumnId<out Any>, PersistableColumnId> {
    override fun toPersistable(source: ColumnId<out Any>): PersistableColumnId {
      return PersistableColumnId(
          id = source.id,
          type = TypeClassEnum.typeOf(source.type)
      )
    }
  }
}