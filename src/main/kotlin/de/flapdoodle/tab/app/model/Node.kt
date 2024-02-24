package de.flapdoodle.tab.app.model

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.calculations.Calculation
import de.flapdoodle.tab.app.model.calculations.Calculations
import de.flapdoodle.tab.app.model.data.Columns
import de.flapdoodle.tab.app.model.data.SingleValues

sealed class Node {
    abstract val name: String
    abstract val id: Id<out Node>
    abstract val position: Position

    interface HasColumns<K: Comparable<K>> {
        val columns: Columns<K>
    }

    interface HasValues {
        val values: SingleValues
    }

    data class Constants(
        override val name: String,
        override val values: SingleValues = SingleValues(),
        override val id: Id<Constants> = Id.nextId(Constants::class),
        override val position: Position = Position(0.0, 0.0)
    ) : Node(), HasValues

    data class Table<K: Comparable<K>> (
        override val name: String,
        override val columns: Columns<K> = Columns(),
        override val id: Id<Table<*>> = Id.nextId(Table::class),
        override val position: Position = Position(0.0, 0.0)
    ) : Node(), HasColumns<K>

    data class Calculated<K: Comparable<K>>(
        override val name: String,
        val calculations: Calculations,
        // result
        override val columns: Columns<K> = Columns(),
        override val values: SingleValues = SingleValues(),
        override val id: Id<Calculated<*>> = Id.nextId(Calculated::class),
        override val position: Position = Position(0.0, 0.0)
    ): Node(), HasColumns<K>, HasValues
}