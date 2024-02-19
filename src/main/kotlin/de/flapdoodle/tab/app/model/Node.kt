package de.flapdoodle.tab.app.model

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.data.Columns
import de.flapdoodle.tab.app.model.data.SingleValues

sealed class Node {
    abstract val name: String
    abstract val id: Id<out Node>

    data class Constants(
        override val name: String,
        val values: SingleValues = SingleValues(),
        override val id: Id<Constants> = Id.nextId(Constants::class)
    ) : Node()

    data class Table(
        override val name: String,
        val columns: Columns = Columns(),
        override val id: Id<Table> = Id.nextId(Table::class)
    ) : Node()
}