package de.flapdoodle.tab.graph.nodes

import de.flapdoodle.tab.bindings.Registration
import de.flapdoodle.tab.bindings.map
import de.flapdoodle.tab.data.CalculatedTable
import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.HasColumns
import de.flapdoodle.tab.data.Model
import de.flapdoodle.tab.data.TableDef
import de.flapdoodle.tab.fx.SingleThreadMutex
import de.flapdoodle.tab.graph.nodes.values.TableDefGraphNode
import de.flapdoodle.tab.types.Id
import javafx.beans.InvalidationListener
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.layout.Pane
import tornadofx.*
import java.util.concurrent.ThreadLocalRandom

class ModelRenderer(private val pane: Pane) {
  private val modelProperty: ObjectProperty<Model> = SimpleObjectProperty(Model())
  private val dataProperty: ObjectProperty<Data> = SimpleObjectProperty(Data())
  private val calculationMutex = SingleThreadMutex()
  private var tableNodes: Map<Id<out HasColumns>, Pair<Registration, TableDefGraphNode>> = emptyMap()

  init {
    modelProperty.addListener(ChangeListener { observable, oldValue, newValue ->
      renderModel(newValue ?: Model())
    })

    dataProperty.addListener(ChangeListener { _,_,newValue ->
      println("data changed: $newValue")
      calculationMutex.tryExecute {
        println("calculate...")
        dataProperty.set(calculate(modelProperty.get(), newValue))
      }
    })

    modelProperty.addListener(InvalidationListener {
      //throw IllegalArgumentException("not implemented")
    })
  }

  fun change(change: (Model) -> Model) {
    modelProperty.set(change(modelProperty.get()))
  }

  fun changeData(change: (Data) -> Data) {
    dataProperty.set(change(dataProperty.get()))
  }

  private fun calculate(model: Model, data: Data): Data {
    var currentData = data
    val calcTables = model.tables().filterIsInstance<CalculatedTable>()
    calcTables.forEach {
      currentData = it.calculate(currentData)
    }
    return currentData
  }

  private fun renderModel(model: Model) {
    println("model: $model")
    val currentTableNodes = tableNodes

    val tablesStillThere = model.tableIds()
    val currentVisibleTables = currentTableNodes.keys

    val removed = currentVisibleTables - tablesStillThere
    val new = tablesStillThere - currentVisibleTables

    println("removed: $removed")
    println("new: $new")

    val nodesToRemove = currentTableNodes.filterKeys { removed.contains(it) }.values

    val nodesToAdd = new.map { tableId ->
      val (registration, tableProperty) = modelProperty.map { m -> m?.table(tableId) }

      val changeListener = when (tableId.type) {
        TableDef::class -> object : ColumnValueChangeListener {
          override fun <T : Any> change(id: ColumnId<out T>, row: Int, value: T?) {
            changeData { d -> d.change(id, row, value) }
          }
        }
        else -> null
      }

      println("tableProp: ${tableProperty.value}")

      val registrationAndNode = registration to TableDefGraphNode(tableProperty, dataProperty, changeListener).apply {
        val x = ThreadLocalRandom.current().nextDouble(0.0, 400.0)
        val y = ThreadLocalRandom.current().nextDouble(0.0, 400.0)
        moveTo(x, y)
        title = "Table (${tableId.id})"
        println("table: ${tableProperty.value}")
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