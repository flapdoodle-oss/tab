package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.Data

data class PersistableData(
    val columnValues: List<PersistableDataEntry>,
    val isSingleValue: List<PersistableColumnId>
) {

  companion object : PersistableAdapter<Data, PersistableData> {
    override fun toPersistable(source: Data): PersistableData {
      return PersistableData(
          columnValues = source.columnValues.map { entry ->
            PersistableDataEntry(
                columnId = PersistableColumnId.toPersistable(entry.key),
                values = PersistableValues.toPersistable(entry.value)
            )
          },
          isSingleValue = source.singleValueColumns.map(PersistableColumnId.Companion::toPersistable)
      )
    }

    override fun from(context: FromPersistableContext, source: PersistableData): Data {
      return Data(
          columnValues = source.columnValues.map {
            val valueType = it.columnId.type.type
            val id = PersistableColumnId.forType(valueType).from(context, it.columnId)
            val values = PersistableValues.forType(valueType).from(context, it.values)

            require(id.type==values.type) {"read type mismatch: $id != ${values.type} -> should be $valueType"}
            id to values
          }.toMap(),
          singleValueColumns = source.isSingleValue.map {
            PersistableColumnId.forType(Any::class).from(context, it)
          }.toSet()
      )
    }
  }

  data class PersistableDataEntry(
      val columnId: PersistableColumnId,
      val values: PersistableValues
  )
}