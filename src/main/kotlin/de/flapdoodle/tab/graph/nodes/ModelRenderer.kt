package de.flapdoodle.tab.graph.nodes

import de.flapdoodle.tab.bindings.ChangingConverter
import de.flapdoodle.tab.bindings.ObjectProperties
import de.flapdoodle.tab.bindings.Registration
import de.flapdoodle.tab.data.Model
import de.flapdoodle.tab.data.Table
import de.flapdoodle.tab.graph.nodes.values.NewValuesNode
import de.flapdoodle.tab.types.Id
import javafx.beans.InvalidationListener
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.scene.layout.Pane
import tornadofx.*
import java.util.concurrent.ThreadLocalRandom

class ModelRenderer(private val pane: Pane) {
  private val modelProperty: ObjectProperty<Model> = SimpleObjectProperty(Model())
  private var tableNodes: Map<Id<Table>, Pair<Registration, NewValuesNode>> = emptyMap()

  init {
    modelProperty.addListener(ChangeListener { observable, oldValue, newValue ->
      renderModel(newValue ?: Model())
    })

    modelProperty.addListener(InvalidationListener {
      //throw IllegalArgumentException("not implemented")
    })
  }

  fun setModel(model: Model) {
    modelProperty.set(model)
  }

  private fun renderModel(model: Model) {
    println("model: $model")
    val currentTableNodes = tableNodes

    val tablesStillThere = model.tables.map { it.id }.toSet()
    val currentVisibleTables = currentTableNodes.keys

    val removed = currentVisibleTables - tablesStillThere
    val new = tablesStillThere - currentVisibleTables

    println("removed: $removed")
    println("new: $new")

    val nodesToRemove = currentTableNodes.filterKeys { removed.contains(it) }.values

    val nodesToAdd = new.map { tableId ->
      val (registration, tableProperty) = ObjectProperties.bidirectionalMappedSync(modelProperty, ChangingConverter<Model, Table>(
          to = { m: Model?, old: Table? -> model.tables.firstOrNull { it.id == tableId } },
          from = { t: Table?, old: Model? ->
            old?.let {
              t?.let { tab ->
                it.changeTable(tableId, { tab })
              } ?: it
            }
          }
      ))

      val registrationAndNode = registration to NewValuesNode(tableProperty).apply {
        val x = ThreadLocalRandom.current().nextDouble(0.0, 400.0)
        val y = ThreadLocalRandom.current().nextDouble(0.0, 400.0)
        moveTo(x, y)
        title = "Table (${tableId.id})"
        println("table: ${tableProperty.get()}")
      }

      tableId to registrationAndNode
    }.toMap()

    nodesToRemove.forEach {
      it.first.remove()
      pane.children.remove(it.second.root)
    }

    tableNodes = tableNodes + nodesToAdd

    nodesToAdd.forEach {
      pane += it.value.second.root
    }
  }
}