package de.flapdoodle.tab.graph.nodes

import de.flapdoodle.tab.bindings.ChangingConverter
import de.flapdoodle.tab.bindings.Converter
import de.flapdoodle.tab.bindings.ObjectProperties
import de.flapdoodle.tab.bindings.RegisteredWritableObservableValue
import de.flapdoodle.tab.data.Model
import de.flapdoodle.tab.data.Table
import de.flapdoodle.tab.graph.nodes.values.NewValuesNode
import de.flapdoodle.tab.types.Id
import javafx.beans.InvalidationListener
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.WritableObjectValue
import javafx.scene.layout.Pane
import tornadofx.*
import java.util.concurrent.ThreadLocalRandom

class ModelRenderer(
    private val modelProperty: ObjectProperty<Model>,
    private val pane: Pane
) {

  private var tableNodes: Map<Id<Table>, NewValuesNode<RegisteredWritableObservableValue<Table>>> = emptyMap()

  init {
    modelProperty.addListener(ChangeListener { observable, oldValue, newValue ->
      setModel(newValue ?: Model())
    })

    modelProperty.addListener(InvalidationListener {
      throw IllegalArgumentException("not implemented")
    })
  }

  private fun setModel(model: Model) {
    val currentTableNodes = tableNodes

    val tablesStillThere = model.tables.map { it.id }.toSet()
    val currentVisibleTables = currentTableNodes.keys

    val removed = currentVisibleTables - tablesStillThere
    val new = tablesStillThere - currentVisibleTables

    val nodesToRemove = currentTableNodes.filterKeys { removed.contains(it) }.values
    val nodesToAdd = model.tables.filter { new.contains(it.id) }
        .map {
          NewValuesNode(SimpleObjectProperty(it)).apply {
            val x = ThreadLocalRandom.current().nextDouble(0.0, 400.0)
            val y = ThreadLocalRandom.current().nextDouble(0.0, 400.0)
            moveTo(x, y)
            title = "Table (${it.id})"
          }
        }

    new.map { tableId ->
      val tableProperty = ObjectProperties.bidirectionalMappedSync(modelProperty, ChangingConverter<Model, Table>(
          to = { m: Model?, old: Table? -> model.tables.firstOrNull { it.id==tableId } },
          from = { t: Table?, old: Model? -> old?.let {
            t?.let {  tab ->
              it.changeTable(tableId, { tab })
            } ?: it
          } }
      ))

      NewValuesNode(tableProperty).apply {
        val x = ThreadLocalRandom.current().nextDouble(0.0, 400.0)
        val y = ThreadLocalRandom.current().nextDouble(0.0, 400.0)
        moveTo(x, y)
        title = "Table (${tableId})"
      }
    }

    nodesToRemove.forEach {
      pane.children.remove(it.root)
    }
    nodesToAdd.forEach {
      pane += it
    }
  }
}