package de.flapdoodle.tab.graph.nodes.values

import de.flapdoodle.tab.bindings.mapToList
import de.flapdoodle.tab.bindings.syncFrom
import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.Table
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableObjectValue
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.layout.VBox
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

class TableNode(
    private val table: ObjectProperty<Table>
) : () -> VBox {

  val rows = table.mapToList(Table::rows)
  val columnList = table.mapToList(Table::columnIds)

  private fun <T : Any> tableColumn(columnId: ColumnId<T>?): TableColumn<Table.Row, T> {
    val ret = TableColumn<Table.Row, T>(columnId!!.name)
    ret.cellValueFactory = Callback {
      val row = it.value
      SimpleObjectProperty(row[columnId]).apply {
        onChange {
          table.value = table.value.change(columnId, row.index, it)
        }
      }
    }
    ret.makeEditable(columnId.type)
    return ret
  }

  override fun invoke(): VBox {
    return VBox().apply {
      val table = tableview(rows) {
        isEditable = true
        columns.syncFrom(columnList) { tableColumn(it) }
      }
    }
  }

  @Suppress("CAST_NEVER_SUCCEEDS", "UNCHECKED_CAST")
  fun <T, S : Any> TableColumn<T, S>.makeEditable(s: KClass<S>) = apply {
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