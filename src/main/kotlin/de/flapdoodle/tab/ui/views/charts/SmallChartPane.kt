package de.flapdoodle.tab.ui.views.charts

import de.flapdoodle.kfx.controls.charts.RangeFactories
import de.flapdoodle.kfx.controls.charts.RangeFactory
import de.flapdoodle.kfx.controls.charts.Serie
import de.flapdoodle.kfx.controls.charts.SmallChart
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.Columns
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.StackPane
import java.time.LocalDate
import java.util.*
import kotlin.reflect.KClass

class SmallChartPane<K : Comparable<K>>(
    node: de.flapdoodle.tab.model.Node.Calculated<K>
) : StackPane() {

    private var columns: Columns<K> = node.columns

    private val series: SimpleObjectProperty<List<Serie<K, Number>>> = SimpleObjectProperty(emptyList())
    private val lineChart = SmallChart<K, Number>(series, indexRangeFactory(node.indexType), RangeFactories.number())

    init {
        series.value = seriesOf(columns)
        
        children.add(lineChart)
    }

    fun update(node: de.flapdoodle.tab.model.Node.Calculated<K>) {
        columns = node.columns
        series.value = seriesOf(columns)
    }


    private fun indexRangeFactory(indexType: KClass<K>): RangeFactory<K> {
        return when (indexType) {
            LocalDate::class -> RangeFactories.localDate() as RangeFactory<K>
            Double::class -> RangeFactories.number() as RangeFactory<K>
            Int::class -> RangeFactories.number() as RangeFactory<K>
            else -> throw IllegalArgumentException("not implemented: $indexType")
        }
    }

    companion object {

        private fun <K : Comparable<K>> seriesOf(columns: Columns<K>): List<Serie<K, Number>> {
            val filtered = columns.filter { Number::class.java.isAssignableFrom(it.valueType.javaObjectType) }
            val index = filtered.index()
            val mapped = filtered.columns().map { c ->
                columAsSeries(index, c as Column<K, out Number>)
            }
            return mapped
        }

        private fun <K : Comparable<K>> columAsSeries(
            index: SortedSet<K>,
            c: Column<K, out Number>
        ): Serie<K, Number> {
            val values = index.map { it to c[it]!! }
            return Serie<K, Number>(c.name, c.color, values)
        }

    }
}