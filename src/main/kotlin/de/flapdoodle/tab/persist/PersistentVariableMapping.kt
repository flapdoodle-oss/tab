package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.nodes.ColumnConnection
import de.flapdoodle.tab.data.nodes.VariableMapping
import de.flapdoodle.tab.data.values.Variable

data class PersistentVariableMapping(
    val variableName: String,
    val variableType: VariableOrColumnType,
    val columnConnection: PersistableColumnConnection
) {

  companion object : PersistableAdapter<VariableMapping<out Any>, PersistentVariableMapping> {
    override fun toPersistable(source: VariableMapping<out Any>): PersistentVariableMapping {
      return PersistentVariableMapping(
          variableName = source.variable.name,
          variableType = TypeClassEnum.typeOf(source.variable.type),
          columnConnection = PersistableColumnConnection.toPersistable(source.columnConnection)
      )
    }

    override fun from(context: FromPersistableContext, source: PersistentVariableMapping): VariableMapping<out Any> {
      return fromCasted(context,source)
    }

    fun <T: Any> fromCasted(context: FromPersistableContext, source: PersistentVariableMapping): VariableMapping<T> {
      @Suppress("UNCHECKED_CAST")
      return VariableMapping(
          variable = Variable(TypeClassEnum.typeClassOf(source.variableType), source.variableName) as Variable<T>,
          columnConnection = PersistableColumnConnection.from(context, source.columnConnection) as ColumnConnection<T>
      )
    }
  }
}