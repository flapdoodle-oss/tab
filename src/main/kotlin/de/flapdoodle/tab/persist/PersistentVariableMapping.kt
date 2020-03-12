package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.nodes.ColumnConnection
import de.flapdoodle.tab.data.nodes.VariableMapping
import de.flapdoodle.tab.data.values.Input

data class PersistentVariableMapping(
    val type: Type,
    val variableName: String,
    val variableType: VariableOrColumnType,
    val columnConnection: PersistableColumnConnection
) {

  enum class Type {
    Variable,
    ListVariable
  }

  companion object : PersistableAdapter<VariableMapping<out Any>, PersistentVariableMapping> {
    override fun toPersistable(source: VariableMapping<out Any>): PersistentVariableMapping {
      return PersistentVariableMapping(
          type = when (source.variable) {
            is Input.Variable -> Type.Variable
            is Input.List -> Type.ListVariable
          },
          variableName = source.variable.name,
          variableType = TypeClassEnum.typeOf(source.variable.type),
          columnConnection = PersistableColumnConnection.toPersistable(source.columnConnection)
      )
    }

    override fun from(context: FromPersistableContext, source: PersistentVariableMapping): VariableMapping<out Any> {
      return fromCasted(context, source)
    }

    fun <T : Any> fromCasted(context: FromPersistableContext, source: PersistentVariableMapping): VariableMapping<T> {
      @Suppress("UNCHECKED_CAST")
      return VariableMapping(
          variable = when (source.type) {
            Type.Variable -> Input.Variable(TypeClassEnum.typeClassOf(source.variableType), source.variableName) as Input.Variable<T>
            Type.ListVariable -> Input.List(TypeClassEnum.typeClassOf(source.variableType), source.variableName) as Input.List<T>
          },
          columnConnection = PersistableColumnConnection.from(context, source.columnConnection) as ColumnConnection<T>
      )
    }
  }
}