package de.flapdoodle.tab.app.ui.views.charts

import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.data.Column
import de.flapdoodle.tab.app.model.data.Columns
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.layout.StackPane
import tornadofx.toObservable
import java.util.*

class ChartPane<K : Comparable<K>>(
    node: Node.Calculated<K>
) : StackPane() {

    private var columns: Columns<K> = node.columns

//    private val indexAxis = IndexAxis<K>()
//    private val indexAxis = NumberAxis()
    private val indexAxis = CategoryAxis()
    private val valueAxis = NumberAxis()
    private val series: ObservableList<XYChart.Series<String, Number>> = FXCollections.observableArrayList()

    private val lineChart = LineChart(indexAxis,valueAxis,series)

    init {
        children.add(lineChart)
    }

    fun update(node: Node.Calculated<K>) {
//        val columnChanges = Diff.diff(columns.columns(), node.columns.columns()) { it.id }

//        // HACK
//        tableColumns.value = tableColumnsOff(indexColumn, node.columns)
//        tableRows.value = rowsOf(node.columns)
        columns = node.columns

        val filtered = columns.filter { Number::class.java.isAssignableFrom(it.valueType.javaObjectType) }

        val index = filtered.index()
        val mapped = filtered.columns().map { c ->
            columAsSeries(index, c as Column<K, out Number>)
        }

        series.setAll(mapped)
//        columns.index().forEach { index ->
//
//        }

    }

    private fun columAsSeries(
        index: SortedSet<K>,
        c: Column<K, out Number>
    ): XYChart.Series<String, Number> {
        val values = index.map { XYChart.Data("$it", c[it]) }
        return XYChart.Series(c.name, values.toObservable())
    }

}