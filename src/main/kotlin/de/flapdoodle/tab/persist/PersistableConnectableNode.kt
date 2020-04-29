package de.flapdoodle.tab.persist

import de.flapdoodle.tab.data.nodes.ConnectableNode

data class PersistableConnectableNode(
    val name: String,
    val type: Type,
    val id: PersistableNodeId,
    val columns: List<PersistableNamedColumn> = emptyList(),
    val calculations: List<PersistableCalculationMapping> = emptyList(),
    val aggregations: List<PersistableAggregationMapping> = emptyList()
) {

  enum class Type {
    Constants,
    Table,
    Calculated,
    Aggregated
  }

  companion object : PersistableAdapter<ConnectableNode, PersistableConnectableNode> {

    override fun toPersistable(source: ConnectableNode): PersistableConnectableNode {
      return when (source) {
        is ConnectableNode.Constants -> PersistableConnectableNode(
            name = source.name,
            type = Type.Constants,
            id = PersistableNodeId.toPersistable(source.id),
            columns = source.columns().map(PersistableNamedColumn.Companion::toPersistable)
        )
        is ConnectableNode.Table -> PersistableConnectableNode(
            name = source.name,
            type = Type.Table,
            id = PersistableNodeId.toPersistable(source.id),
            columns = source.columns().map(PersistableNamedColumn.Companion::toPersistable)
        )
        is ConnectableNode.Calculated -> PersistableConnectableNode(
            name = source.name,
            type = Type.Calculated,
            id = PersistableNodeId.toPersistable(source.id),
            calculations = source.calculations().map(PersistableCalculationMapping.Companion::toPersistable)
        )
        is ConnectableNode.Aggregated -> PersistableConnectableNode(
            name = source.name,
            type = Type.Aggregated,
            id = PersistableNodeId.toPersistable(source.id),
            aggregations = source.aggregations().map(PersistableAggregationMapping.Companion::toPersistable)
        )
      }
    }

    override fun from(context: FromPersistableContext, source: PersistableConnectableNode): ConnectableNode {
      return when (source.type) {
        Type.Constants -> ConnectableNode.Constants(
            name = source.name,
            id = PersistableNodeId.FromConstantsId.from(context, source.id),
            columns = source.columns.map { PersistableNamedColumn.from(context, it) }
        )
        Type.Table -> ConnectableNode.Table(
            name = source.name,
            id = PersistableNodeId.FromTableId.from(context, source.id),
            columns = source.columns.map { PersistableNamedColumn.from(context, it) }
        )
        Type.Calculated -> ConnectableNode.Calculated(
            name = source.name,
            id = PersistableNodeId.FromCalculatedId.from(context, source.id),
            calculations = source.calculations.map { PersistableCalculationMapping.from(context, it) }
        )
        Type.Aggregated -> ConnectableNode.Aggregated(
            name = source.name,
            id = PersistableNodeId.FromAggregatedId.from(context, source.id),
            aggregations = source.aggregations.map { PersistableAggregationMapping.from(context, it) }
        )
      }
    }
  }
}