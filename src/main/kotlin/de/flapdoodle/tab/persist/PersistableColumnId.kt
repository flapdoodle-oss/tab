package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.ColumnId
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

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

    fun <T : Any> forType(type: KClass<T>): FromPersistable<ColumnId<T>, PersistableColumnId> {
      return FromPersistable { context, source ->
        require(source.type.type.isSubclassOf(type)) { "type mismatch: $type != ${source.type.type}" }
        @Suppress("UNCHECKED_CAST")
        context.columnIdFor(source.id, source.type.type as KClass<T>)
      }
    }
  }
}