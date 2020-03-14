package de.flapdoodle.tab.graph.nodes.renderer

import de.flapdoodle.tab.data.Data
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.nodes.HasColumns
import de.flapdoodle.tab.data.values.Values
import de.flapdoodle.tab.lazy.LazyValue
import de.flapdoodle.tab.lazy.asListBinding
import de.flapdoodle.tab.lazy.map
import de.flapdoodle.tab.lazy.merge
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
  private val streams = node.map(HasColumns::columns)

  override val root = accordion {
    this.panes += titledpane("Chart") {
      isExpanded = false
      isAnimated = false
      linechart("TODO", CategoryAxis(), NumberAxis()) {
        minWidth = 50.0
        minHeight = 20.0
        maxHeight = 150.0

        multiseries("Product X", "Product Y") {
          data("MAR", 10245, 28443)
          data("APR", 23963, 22845)
          data("MAY", 15038, 19045)
        }

      }
    }

    this.panes += titledpane("Second") {
      isExpanded = false
      isAnimated = false

      val lineChart = LineChart(CategoryAxis(), NumberAxis())
      var chartData: ObservableList<XYChart.Series<String, Number>> = FXCollections.observableArrayList()
      val x = node.merge(data) { node, d ->
        val columns = node.columns()
        val chartColumns = columns.filter { it.id.type==BigDecimal::class }

        if (false) {
          val data1 = FXCollections.observableArrayList<XYChart.Data<String, Number>>()
          data1.add(XYChart.Data<String, Number>("Foo", BigDecimal.valueOf(12.0)))
          data1.add(XYChart.Data<String, Number>("Bar", BigDecimal.valueOf(12.0)))
          val entry = XYChart.Series<String, Number>("A", data1)
          listOf(entry)
        }

        val valueMap = chartColumns.map { it to d[it.id] }.toMap()
        val size = valueMap.map { it.value.size() }.max() ?: 0

        chartColumns.map {
          //val values = FXCollections.observableArrayList<XYChart.Data<String, Number>>()
          val v = d[it.id] as Values<BigDecimal>
          val values = (0 until size).mapIndexed { index, it ->
            XYChart.Data<String, Number>("$index", v[it])
          }.asObservable()
          XYChart.Series(it.name, values)
        }
      }.asListBinding()

      lineChart.data = x
      this += lineChart
    }
  }
}