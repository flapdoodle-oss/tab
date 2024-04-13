package de.flapdoodle.tab.app.ui.views.charts

import de.flapdoodle.kfx.controls.charts.RangeFactories
import de.flapdoodle.kfx.controls.charts.RangeFactory
import de.flapdoodle.kfx.controls.charts.Serie
import de.flapdoodle.kfx.controls.charts.SmallChart
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.data.Column
import de.flapdoodle.tab.app.model.data.Columns
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.StackPane
import java.time.LocalDate
import java.util.*
import kotlin.reflect.KClass

class SmallChartPane<K : Comparable<K>>(
    node: Node.Calculated<K>
) : StackPane() {

    private var columns: Columns<K> = node.columns

    private val series: SimpleObjectProperty<List<Serie<K, Number>>> = SimpleObjectProperty(emptyList())
    private val lineChart = SmallChart<K, Number>(series, indexRangeFactory(node.indexType), RangeFactories.number())

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

        series.value = mapped
//        columns.index().forEach { index ->
//
//        }

    }

    private fun columAsSeries(
        index: SortedSet<K>,
        c: Column<K, out Number>
    ): Serie<K, Number> {
        val values = index.map { it to c[it]!! }
        return Serie<K, Number>(c.name, javafx.scene.paint.Color.RED, values)
    }


    private fun indexRangeFactory(indexType: KClass<K>): RangeFactory<K> {
        return when (indexType) {
            LocalDate::class -> RangeFactories.localDate() as RangeFactory<K>
            Int::class -> RangeFactories.number() as RangeFactory<K>
            else -> throw IllegalArgumentException("not implemented: $indexType")
        }
    }
}