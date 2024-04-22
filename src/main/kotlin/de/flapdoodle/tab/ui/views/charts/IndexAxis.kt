package de.flapdoodle.tab.ui.views.charts

import javafx.scene.chart.Axis

class IndexAxis<K: Comparable<K>> : Axis<K>() {
    override fun autoRange(length: Double): Any? {
        return null
//        TODO("Not yet implemented")
    }

    override fun setRange(range: Any?, animate: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getRange(): Any {
        TODO("Not yet implemented")
    }

    override fun getZeroPosition(): Double {
        TODO("Not yet implemented")
    }

    override fun getValueForDisplay(displayPosition: Double): K {
        TODO("Not yet implemented")
    }

    override fun toRealValue(value: Double): K {
        TODO("Not yet implemented")
    }

    override fun calculateTickValues(length: Double, range: Any?): MutableList<K> {
        TODO("Not yet implemented")
    }

    override fun getTickMarkLabel(value: K): String {
        TODO("Not yet implemented")
    }

    override fun toNumericValue(value: K): Double {
        TODO("Not yet implemented")
    }

    override fun isValueOnAxis(value: K): Boolean {
        TODO("Not yet implemented")
    }

    override fun getDisplayPosition(value: K): Double {
        TODO("Not yet implemented")
    }
}