package de.flapdoodle.tab.app.model

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.data.Columns
import de.flapdoodle.tab.app.model.data.SingleValues

sealed class Node {
    abstract val name: String
    abstract val id: Id<out Node>

    interface HasColumns<K: Comparable<K>> {
        val columns: Columns<K>
    }

    interface HasValues {
        val values: SingleValues
    }

    data class Constants(
        override val name: String,
        override val values: SingleValues = SingleValues(),
        override val id: Id<Constants> = Id.nextId(Constants::class)
    ) : Node(), HasValues

    data class Table<K: Comparable<K>> (
        override val name: String,
        override val columns: Columns<K> = Columns(),
        override val id: Id<Table<*>> = Id.nextId(Table::class)
    ) : Node(), HasColumns<K>

    data class Calculated<K: Comparable<K>>(
        override val name: String,
        val inputs: Inputs = Inputs(),
        override val columns: Columns<K> = Columns(),
        override val values: SingleValues = SingleValues(),
        override val id: Id<Calculated<*>> = Id.nextId(Calculated::class)
    ): Node(), HasColumns<K>, HasValues
}