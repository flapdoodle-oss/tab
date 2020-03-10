package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.Data

data class PersistableData(
    val columnValues: List<PersistableDataEntry>
) {

  companion object : ToPersistable<Data, PersistableData> {
    override fun toPersistable(source: Data): PersistableData {
      return PersistableData(
          columnValues = source.columnValues.map { entry ->
            PersistableDataEntry(
                columnId = PersistableColumnId.toPersistable(entry.key),
                values = PersistableValues.toPersistable(entry.value)
            )
          }
      )
    }

  }

  data class PersistableDataEntry(
      val columnId: PersistableColumnId,
      val values: PersistableValues
  )
}