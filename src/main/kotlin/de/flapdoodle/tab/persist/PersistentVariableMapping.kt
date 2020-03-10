package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.nodes.VariableMapping

data class PersistentVariableMapping(
    val variableName: String,
    val variableType: VariableOrColumnType,
    val columnConnection: PersistableColumnConnection
) {

  companion object : ToPersistable<VariableMapping<out Any>, PersistentVariableMapping> {
    override fun toPersistable(source: VariableMapping<out Any>): PersistentVariableMapping {
      return PersistentVariableMapping(
          variableName = source.variable.name,
          variableType = TypeClassEnum.typeOf(source.variable.type),
          columnConnection = PersistableColumnConnection.toPersistable(source.columnConnection)
      )
    }
  }
}