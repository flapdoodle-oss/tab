package de.flapdoodle.tab.model.calculations.interpolation.linear

import java.time.LocalDate

fun interface LinearFactor<K: Any, F: Any> {
    fun factor(start: K, end: K, offset: K): F

    companion object {
        val IntFactor: LinearFactor<Int, Double> = LinearFactor { start, end, offset -> (offset - start) * 1.0 / (end - start) }
        val LocalDateFactor: LinearFactor<LocalDate, Double> = LinearFactor { start, end, offset -> (offset.toEpochDay() - start.toEpochDay()) * 1.0 / (end.toEpochDay() - start.toEpochDay()) }
        val EnumFactor: LinearFactor<Enum<*>, Double> = LinearFactor { start, end, offset -> (offset.ordinal - start.ordinal) * 1.0 / (end.ordinal - start.ordinal) }
    }
}