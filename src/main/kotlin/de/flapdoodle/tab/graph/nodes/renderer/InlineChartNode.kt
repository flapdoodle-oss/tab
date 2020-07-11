package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.HasColumns
import de.flapdoodle.fx.lazy.LazyValue
import de.flapdoodle.fx.lazy.asListBinding
import de.flapdoodle.fx.lazy.map
import de.flapdoodle.fx.lazy.merge
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import tornadofx.*
import java.math.BigDecimal

class InlineChartNode<T>(
    private val node: LazyValue<T>,
    private val data: LazyValue<Data>
) : Fragment()
    where T : HasColumns,
          T : ConnectableNode {
  private val valueColumns = node.map(HasColumns::columns).map {
    @Suppress("UNCHECKED_CAST")
    it.filter { it.id.type == BigDecimal::class }
        .map { it as NamedColumn<BigDecimal> }
  }

  private val columnValues = valueColumns.merge(data) { columns, d ->
    columns.map { it to d[it.id] }
  }

  private val chartData = columnValues.map { list ->
    val max = list.map { (column, values) -> values.size() }.max() ?: 0
    list.mapNotNull { (column, values) ->
      val v = (0 until max).map { values[it] }
      val notNull = v.mapNotNull { it }
      if (notNull.size == v.size) {
        XYChart.Series(column.name, notNull.mapIndexed { index, it ->
          XYChart.Data<String, Number>("${index + 1}", it)
        }.toObservable())
      } else null
    }
  }.asListBinding()

  override val root = LineChart(CategoryAxis(), NumberAxis()).apply {
//    this.data.bindFrom(columnValues) {
//
//    }
    this.data = chartData
  }
}