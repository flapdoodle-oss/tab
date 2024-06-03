package de.flapdoodle.tab.ui.views.charts

import de.flapdoodle.kfx.types.ranges.RangeFactories
import de.flapdoodle.kfx.types.ranges.RangeFactory
import de.flapdoodle.kfx.controls.charts.Serie
import de.flapdoodle.kfx.controls.charts.SmallChart
import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.Columns
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.StackPane
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.util.*
import kotlin.reflect.KClass

class SmallChartPane<K : Comparable<K>>(
    node: de.flapdoodle.tab.model.Node.HasColumns<K>
) : StackPane() {

    private var columns: Columns<K> = node.columns

    private val series: SimpleObjectProperty<List<Serie<K, BigDecimal>>> = SimpleObjectProperty(emptyList())
    private val indexRangeFactory = indexRangeFactory(node.indexType)

    private val lineChart = if (indexRangeFactory!=null) SmallChart(series,
        indexRangeFactory,
        RangeFactories.number(BigDecimal::class),
        de.flapdoodle.tab.ui.Converters.validatingConverter(node.indexType),
        de.flapdoodle.tab.ui.Converters.validatingConverter(BigDecimal::class))
    else null

    init {
        series.value = seriesOf(columns)

        if (lineChart!=null) {
            children.add(lineChart)
        }
    }

    fun update(node: de.flapdoodle.tab.model.Node.HasColumns<K>) {
        columns = node.columns
        series.value = seriesOf(columns)
    }


    private fun indexRangeFactory(indexType: TypeInfo<K>): RangeFactory<K> {
        // TODO umbauen
        return when (indexType) {
            TypeInfo.of(LocalDate::class.java) -> RangeFactories.localDate() as RangeFactory<K>
            TypeInfo.of(Double::class.javaObjectType) -> RangeFactories.number(Double::class) as RangeFactory<K>
            TypeInfo.of(Int::class.javaObjectType) -> RangeFactories.number(Int::class) as RangeFactory<K>
            TypeInfo.of(String::class.java) -> RangeFactories.category()
            else -> throw IllegalArgumentException("not implemented: $indexType")
        }
    }

    companion object {
        private val numberType = TypeInfo.of(Number::class.java)

        private fun <K : Comparable<K>> seriesOf(columns: Columns<K>): List<Serie<K, BigDecimal>> {
            val filtered = columns.filter { numberType.isAssignable(it.valueType) }
            val index = filtered.index()
            val mapped = filtered.columns().map { c ->
                columAsSeries(index, c as Column<K, out Number>)
            }
            return mapped
        }

        private fun <K : Comparable<K>> columAsSeries(
            index: SortedSet<K>,
            c: Column<K, out Number>
        ): Serie<K, BigDecimal> {
            val values = index.map { it to c[it] }
                .filter { it.second != null }
                .map { it.first to bigDecimalOf(it.second!!) }
            return Serie<K, BigDecimal>(c.name.long, c.color, values)
        }

        private fun bigDecimalOf(number: Number): BigDecimal {
            return when (number) {
                is BigDecimal -> number
                is BigInteger -> number.toBigDecimal()
                is Long -> BigDecimal.valueOf(number)
                is Double -> BigDecimal.valueOf(number)
                else -> BigDecimal.valueOf(number.toDouble())
            }
        }

    }
}