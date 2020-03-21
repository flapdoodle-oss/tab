package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.controls.tables.SmartCell
import de.flapdoodle.tab.controls.tables.SmartColumn
import de.flapdoodle.tab.controls.tables.SmartTable
import de.flapdoodle.tab.converter.Converters
import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.HasColumns
import de.flapdoodle.tab.extensions.property
import de.flapdoodle.tab.extensions.subscribeEvent
import de.flapdoodle.tab.graph.nodes.renderer.events.DataEvent
import de.flapdoodle.tab.graph.nodes.renderer.events.ExplainEvent
import de.flapdoodle.tab.lazy.LazyValue
import de.flapdoodle.tab.lazy.asAObservable
import de.flapdoodle.tab.lazy.asListBinding
import de.flapdoodle.tab.lazy.map
import de.flapdoodle.tab.lazy.mapList
import de.flapdoodle.tab.lazy.merge
import de.flapdoodle.tab.lazy.syncFrom
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.ContextMenu
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.util.Callback
import javafx.util.StringConverter
import javafx.util.converter.BigDecimalStringConverter
import javafx.util.converter.BigIntegerStringConverter
import javafx.util.converter.DefaultStringConverter
import javafx.util.converter.DoubleStringConverter
import javafx.util.converter.FloatStringConverter
import javafx.util.converter.IntegerStringConverter
import javafx.util.converter.LocalDateStringConverter
import javafx.util.converter.LocalDateTimeStringConverter
import javafx.util.converter.LocalTimeStringConverter
import javafx.util.converter.LongStringConverter
import javafx.util.converter.NumberStringConverter
import tornadofx.*
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.reflect.KClass

class SmartColumnsNode<T>(
    node: LazyValue<T>,
    data: LazyValue<Data>,
    private val columnHeader: ((NamedColumn<out Any>) -> Fragment)? = null,
    private val columnFooter: ((SmartColumn<Data.Row, *>) -> Fragment)? = null,
    private val editable: Boolean = false,
    private val menu: (ContextMenu.() -> Unit)? = null
) : Fragment()
    where T : HasColumns,
          T : ConnectableNode {

  init {
    require(node.value() != null) { "node is null" }
  }

  private val columnList = node.map(HasColumns::columns)
  private val rows = node.merge(data) { t, d ->
    d to t.columns().map { it.id }
  }.map {
    appendEmptyRow(it.first.rows(it.second))
  }.asListBinding()

  private fun appendEmptyRow(rows: List<Data.Row>): List<Data.Row> {
    return if (editable)
      rows + Data.Row(rows.size, emptyMap())
    else
      rows
  }

  override val root = vbox {

    if (menu != null) {
      contextmenu(menu)
    }

    val table = SmartTable(
        rows = rows,
        columns = columnList.mapList { tableColumn(it) }.asListBinding()
    ).apply {
//      isEditable = editable
      vgrow = Priority.ALWAYS

      //columns().syncFrom(columnList) { tableColumn(it) }
    }

    tabpane {
      vgrow = Priority.ALWAYS
      tab("Data") {
        isClosable = false
        this += table
      }
      tab("Chart") {
        isClosable = false
        this += InlineChartNode(node, data)
      }
    }

//    val table = tableview(rows) {
//      isEditable = editable
//      vgrow = Priority.ALWAYS
//
//      columns.syncFrom(columnList) { tableColumn(it) }
//    }

    if (columnFooter != null) {
      flowpane {
//        maxWidth = Control.USE_PREF_SIZE
        val factory = columnFooter
        children.syncFrom(table.columns()) {
          factory(it!!).root
        }
      }
    }
  }

  private fun <T : Any> tableColumn(namedColumn: NamedColumn<out T>): SmartColumn<Data.Row, T> {
    val header = if (columnHeader != null) {
      hbox {
        label(namedColumn.name)
        this += columnHeader.invoke(namedColumn).root

        maxWidth=Control.USE_PREF_SIZE
      }
    } else {
      label(namedColumn.name)
    }

    return object : SmartColumn<Data.Row, T>(header) {

      init {
        property(ColumnId::class, namedColumn.id)
      }

      override fun cell(row: Data.Row): SmartCell<Data.Row, T> {
        return object : SmartCell<Data.Row, T>(row[namedColumn.id], editable, Converters.converterFor(namedColumn.id.type)) {

          init {
            subscribeEvent<ExplainEvent> { event ->
              when (event.data) {
                is ExplainEvent.EventData.ColumnSelected<out Any> -> {
                  if (event.data.id == namedColumn.id) {
                    style {
                      backgroundColor += Color(0.0, 0.0, 0.0, 0.1)
//                    borderWidth += box(0.0.px, 1.0.px)
//                    borderColor += box(Color.RED)
                    }
                  }
                }
                is ExplainEvent.EventData.NoColumnSelected -> {
                  style {
                    backgroundColor = multi()
//                  borderWidth = multi()
//                  borderColor = multi()
                  }
                }
              }

            }
          }

          override fun onChange(value: T?) {
            fire(DataEvent.EventData.Changed(namedColumn.id, row.index, value).asEvent())
          }
        }
      }
    }
  }

  class XFlowPane : FlowPane() {

    override fun computePrefWidth(forHeight: Double): Double {
      val ret = super.computePrefWidth(forHeight)
      println("FlowPane.computePrefWidth -> $ret")

      println("-> ${minWidth}, ${maxWidth}")
      return ret
    }
  }
}