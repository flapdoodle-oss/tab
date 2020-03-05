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
import de.flapdoodle.tab.extensions.subscribeEvent
import de.flapdoodle.tab.graph.nodes.connections.Out
import de.flapdoodle.tab.graph.nodes.connections.OutNode
import de.flapdoodle.tab.graph.nodes.renderer.events.DataEvent
import de.flapdoodle.tab.graph.nodes.renderer.events.ExplainEvent
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.layout.Priority
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

class ColumnsNode<T>(
    node: ObservableValue<T>,
    data: ObservableValue<Data>,
    private val columnHeader: ((TableColumn<Data.Row,*>) -> Fragment)? = null,
    private val columnFooter: (TableColumn<Data.Row,*>) -> Fragment,
    private val editable: Boolean = false
) : Fragment()
    where T : HasColumns,
          T : ConnectableNode {

  init {
    require(node.value != null) { "node is null" }
  }

  private val columnList = node.mapToList(HasColumns::columns)
  private val rows = node.mergeWith(data) { t, d ->
    d to t.columns().map { it.id }
  }.mapToList {
    it.first.rows(it.second)
  }

  class AskForType : View() {
    override val root = borderpane {
      center {
        button {
          text = "woohooo"
          action {
            close()
          }
        }
      }
    }
  }

  override val root = vbox {
    contextmenu {
      item("Add Column").action {
        find<AskForType>().openModal()
        //selectedItem?.apply { println("Sending Email to $name") }
      }
    }

    val table = TableView(rows).apply {
      isEditable = editable
      vgrow = Priority.ALWAYS

      columns.syncFrom(columnList) { tableColumn(it) }
    }
    if (columnHeader!=null) {
      hbox {
        val factory = columnHeader
        children.syncFrom(table.columns) {
          factory(it!!).root
        }
      }
    }

    this += table

//    val table = tableview(rows) {
//      isEditable = editable
//      vgrow = Priority.ALWAYS
//
//      columns.syncFrom(columnList) { tableColumn(it) }
//    }

    hbox {
      children.syncFrom(table.columns) {
        columnFooter(it!!).root
      }
    }
  }

  private fun <T : Any> tableColumn(namedColumn: NamedColumn<out T>?): TableColumn<Data.Row, T> {
    val ret = TableColumn<Data.Row, T>(namedColumn!!.name)
    ret.property(ColumnId::class, namedColumn.id)

    ret.apply {
      value {
        val row = it.value
        SimpleObjectProperty(row[namedColumn.id]).apply {
          if (editable) {
            onChange { value ->
              fire(DataEvent.EventData.Changed(namedColumn.id, row.index, value).asEvent())
            }
          }
        }
      }
      isEditable = editable
      isReorderable = false
      isSortable = false
//        cellFactory = cellFactoryForType(namedColumn.id.type)
      cellFactory = Callback {
        TextFieldTableCell<Data.Row, T>(converterFor(namedColumn.id.type)).apply {

          subscribeEvent<ExplainEvent> {event ->
            when (event.data) {
              is ExplainEvent.EventData.ColumnSelected<out Any> -> {
                if (event.data.id==namedColumn.id) {
                  style {
                    backgroundColor += Color(0.0,0.0,0.0,0.1)
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

//          style {
//            //backgroundColor += Color.BLUE
//            borderWidth += box(0.0.px, 1.0.px)
////              borderWidth = multi(box(1.0.px), box(0.0.px))
//            borderColor += box(Color.RED)
//          }
        }
      }
    }

//    ret.isReorderable = false
//    ret.isSortable = false
//    if (editable) {
//      ret.makeEditable(namedColumn.id.type)
//    }
    return ret
  }

  private fun <T : Any> tableColumnOLD(column: NamedColumn<out T>?): TableColumn<Data.Row, T> {
    val ret = TableColumn<Data.Row, T>(column!!.name)
    ret.property(ColumnId::class, column.id)

    if (false) {
      ret.apply {
        cellFormat {
          val x = it
          SimpleObjectProperty<Data.Row>()
        }
      }
    }

//    ret.cellFactory = Callback {
//      SmartTableCell(FX.defaultScope, it)
//    }

    ret.cellValueFactory = Callback {
      val row = it.value
      SimpleObjectProperty(row[column.id]).apply {
        if (editable) {
          onChange { value ->
            fire(DataEvent.EventData.Changed(column.id, row.index, value).asEvent())
//            changeListener.change(column.id, row.index, value)
          }
        }
      }
    }
    ret.isReorderable = false
    ret.isSortable = false
    if (editable) {
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

  private fun <T, S : Any> cellFactoryForType(s: KClass<out S>): Callback<TableColumn<T, S>, TableCell<T, S>> {
    @Suppress("UNCHECKED_CAST")
    return when (s.javaPrimitiveType ?: s) {
      Int::class -> TextFieldTableCell.forTableColumn<T, S>(IntegerStringConverter() as StringConverter<S>)
      Integer::class -> TextFieldTableCell.forTableColumn<T, S>(IntegerStringConverter() as StringConverter<S>)
      Integer::class.javaPrimitiveType -> TextFieldTableCell.forTableColumn<T, S>(IntegerStringConverter() as StringConverter<S>)
      Double::class -> TextFieldTableCell.forTableColumn<T, S>(DoubleStringConverter() as StringConverter<S>)
      Double::class.javaPrimitiveType -> TextFieldTableCell.forTableColumn<T, S>(DoubleStringConverter() as StringConverter<S>)
      Float::class -> TextFieldTableCell.forTableColumn<T, S>(FloatStringConverter() as StringConverter<S>)
      Float::class.javaPrimitiveType -> TextFieldTableCell.forTableColumn<T, S>(FloatStringConverter() as StringConverter<S>)
      Long::class -> TextFieldTableCell.forTableColumn<T, S>(LongStringConverter() as StringConverter<S>)
      Long::class.javaPrimitiveType -> TextFieldTableCell.forTableColumn<T, S>(LongStringConverter() as StringConverter<S>)
      Number::class -> TextFieldTableCell.forTableColumn<T, S>(NumberStringConverter() as StringConverter<S>)
      BigDecimal::class -> TextFieldTableCell.forTableColumn<T, S>(BigDecimalStringConverter() as StringConverter<S>)
      BigInteger::class -> TextFieldTableCell.forTableColumn<T, S>(BigIntegerStringConverter() as StringConverter<S>)
      String::class -> TextFieldTableCell.forTableColumn<T, S>(DefaultStringConverter() as StringConverter<S>)
      LocalDate::class -> TextFieldTableCell.forTableColumn<T, S>(LocalDateStringConverter() as StringConverter<S>)
      LocalTime::class -> TextFieldTableCell.forTableColumn<T, S>(LocalTimeStringConverter() as StringConverter<S>)
      LocalDateTime::class -> TextFieldTableCell.forTableColumn<T, S>(LocalDateTimeStringConverter() as StringConverter<S>)
//      Boolean::class.javaPrimitiveType -> {
//        (this as TableColumn<T, Boolean?>).useCheckbox(true)
//      }
      else -> throw RuntimeException("makeEditable() is not implemented for specified class type:" + s.qualifiedName)
    }
  }

  private fun <S : Any> converterFor(s: KClass<out S>): StringConverter<S> {
    @Suppress("UNCHECKED_CAST")
    return when (s.javaPrimitiveType ?: s) {
      Int::class -> IntegerStringConverter() as StringConverter<S>
      Integer::class -> IntegerStringConverter() as StringConverter<S>
      Integer::class.javaPrimitiveType -> IntegerStringConverter() as StringConverter<S>
      Double::class -> DoubleStringConverter() as StringConverter<S>
      Double::class.javaPrimitiveType -> DoubleStringConverter() as StringConverter<S>
      Float::class -> FloatStringConverter() as StringConverter<S>
      Float::class.javaPrimitiveType -> FloatStringConverter() as StringConverter<S>
      Long::class -> LongStringConverter() as StringConverter<S>
      Long::class.javaPrimitiveType -> LongStringConverter() as StringConverter<S>
      Number::class -> NumberStringConverter() as StringConverter<S>
      BigDecimal::class -> BigDecimalStringConverter() as StringConverter<S>
      BigInteger::class -> BigIntegerStringConverter() as StringConverter<S>
      String::class -> DefaultStringConverter() as StringConverter<S>
      LocalDate::class -> LocalDateStringConverter() as StringConverter<S>
      LocalTime::class -> LocalTimeStringConverter() as StringConverter<S>
      LocalDateTime::class -> LocalDateTimeStringConverter() as StringConverter<S>
//      Boolean::class.javaPrimitiveType -> {
//        (this as TableColumn<T, Boolean?>).useCheckbox(true)
//      }
      else -> throw RuntimeException("makeEditable() is not implemented for specified class type:" + s.qualifiedName)
    }
  }
}