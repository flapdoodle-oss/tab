package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.bindings.mapToList
import de.flapdoodle.tab.bindings.mergeWith
import de.flapdoodle.tab.bindings.syncFrom
import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.HasColumns
import de.flapdoodle.tab.extensions.property
import de.flapdoodle.tab.graph.nodes.ColumnValueChangeListener
import de.flapdoodle.tab.graph.nodes.connections.Out
import de.flapdoodle.tab.graph.nodes.connections.OutNode
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.layout.Priority
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

class ColumnsNode<T>(
    node: ObservableValue<T>,
    data: ObservableValue<Data>,
    private val changeListener: ColumnValueChangeListener? = null

) : Fragment()
    where T : HasColumns,
          T : ConnectableNode {

  init {
    require(node.value!=null) {"node is null"}
  }

  private val columnList = node.mapToList(HasColumns::columns)
  private val rows = node.mergeWith(data) { t, d ->
    d to t.columns().map { it.id }
  }.second.mapToList {
    it.first.rows(it.second)
  }

  override val root = vbox {
    val table = tableview(rows) {
      isEditable = changeListener != null
      vgrow = Priority.ALWAYS
      columns.syncFrom(columnList) { tableColumn(it) }
    }

    hbox {
      children.syncFrom(table.columns) {
        val columnId = (it!!.property(ColumnId::class)
            ?: throw IllegalArgumentException("columnId not set"))

        OutNode(Out.Aggregate(columnId)).apply {
          prefWidthProperty().bind(it.widthProperty())
        }
      }
    }

  }

  private fun <T : Any> tableColumn(column: NamedColumn<out T>?): TableColumn<Data.Row, T> {
    val ret = TableColumn<Data.Row, T>(column!!.name)
    ret.property(ColumnId::class, column.id)

    ret.cellValueFactory = Callback {
      val row = it.value
      SimpleObjectProperty(row[column.id]).apply {
        if (changeListener != null) {
          onChange { value ->
            changeListener.change(column.id, row.index, value)
          }
        }
      }
    }
    ret.isReorderable = false
    if (changeListener != null) {
      ret.makeEditable(column.id.type)
    }
    return ret
  }

  @Suppress("CAST_NEVER_SUCCEEDS", "UNCHECKED_CAST")
  private fun <T, S : Any> TableColumn<T, S>.makeEditable(s: KClass<out S>) = apply {
    //tableView?.isEditable = true
    isEditable = true
    when (s.javaPrimitiveType ?: s) {
      Int::class -> cellFactory = TextFieldTableCell.forTableColumn<T, S>(IntegerStringConverter() as StringConverter<S>)
      Integer::class -> cellFactory = TextFieldTableCell.forTableColumn<T, S>(IntegerStringConverter() as StringConverter<S>)
      Integer::class.javaPrimitiveType -> cellFactory = TextFieldTableCell.forTableColumn<T, S>(IntegerStringConverter() as StringConverter<S>)
      Double::class -> cellFactory = TextFieldTableCell.forTableColumn<T, S>(DoubleStringConverter() as StringConverter<S>)
      Double::class.javaPrimitiveType -> cellFactory = TextFieldTableCell.forTableColumn<T, S>(DoubleStringConverter() as StringConverter<S>)
      Float::class -> cellFactory = TextFieldTableCell.forTableColumn<T, S>(FloatStringConverter() as StringConverter<S>)
      Float::class.javaPrimitiveType -> cellFactory = TextFieldTableCell.forTableColumn<T, S>(FloatStringConverter() as StringConverter<S>)
      Long::class -> cellFactory = TextFieldTableCell.forTableColumn<T, S>(LongStringConverter() as StringConverter<S>)
      Long::class.javaPrimitiveType -> cellFactory = TextFieldTableCell.forTableColumn<T, S>(LongStringConverter() as StringConverter<S>)
      Number::class -> cellFactory = TextFieldTableCell.forTableColumn<T, S>(NumberStringConverter() as StringConverter<S>)
      BigDecimal::class -> cellFactory = TextFieldTableCell.forTableColumn<T, S>(BigDecimalStringConverter() as StringConverter<S>)
      BigInteger::class -> cellFactory = TextFieldTableCell.forTableColumn<T, S>(BigIntegerStringConverter() as StringConverter<S>)
      String::class -> cellFactory = TextFieldTableCell.forTableColumn<T, S>(DefaultStringConverter() as StringConverter<S>)
      LocalDate::class -> cellFactory = TextFieldTableCell.forTableColumn<T, S>(LocalDateStringConverter() as StringConverter<S>)
      LocalTime::class -> cellFactory = TextFieldTableCell.forTableColumn<T, S>(LocalTimeStringConverter() as StringConverter<S>)
      LocalDateTime::class -> cellFactory = TextFieldTableCell.forTableColumn<T, S>(LocalDateTimeStringConverter() as StringConverter<S>)
      Boolean::class.javaPrimitiveType -> {
        (this as TableColumn<T, Boolean?>).useCheckbox(true)
      }
      else -> throw RuntimeException("makeEditable() is not implemented for specified class type:" + s.qualifiedName)
    }
  }
}