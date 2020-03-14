package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.HasColumns
import de.flapdoodle.tab.data.values.Values
import de.flapdoodle.tab.lazy.LazyValue
import de.flapdoodle.tab.lazy.asListBinding
import de.flapdoodle.tab.lazy.map
import de.flapdoodle.tab.lazy.mapList
import de.flapdoodle.tab.lazy.merge
import de.flapdoodle.tab.lazy.syncFrom
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Parent
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import tornadofx.*
import java.math.BigDecimal

class ChartNode<T>(
    private val node: LazyValue<T>,
    private val data: LazyValue<Data>
) : Fragment()
    where T : HasColumns,
          T : ConnectableNode
{
  private val valueColumns = node.map(HasColumns::columns).map {
    @Suppress("UNCHECKED_CAST")
    it.filter { it.id.type==BigDecimal::class }
        .map { it as NamedColumn<BigDecimal> }
  }

  private val columnValues = valueColumns.merge(data) { columns, d ->
    columns.map { it to d[it.id] }
  }

  private val chartData = columnValues.map { list ->
    val max = list.map { (column, values) -> values.size() }.max() ?: 0
    list.map { (column, values) ->
      val list = (0 until max).map { XYChart.Data<String, Number>("$it", values[it]) }
      XYChart.Series<String, Number>(column.name, list.toObservable())
    }
  }.asListBinding()

  override val root = accordion {
    this.panes += titledpane("Second") {
      isExpanded = false
      isAnimated = false

      val lineChart = LineChart(CategoryAxis(), NumberAxis())
      lineChart.data = chartData

      this += lineChart
    }
  }
}