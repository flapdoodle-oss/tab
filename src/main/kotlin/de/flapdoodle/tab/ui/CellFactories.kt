package de.flapdoodle.tab.ui

import de.flapdoodle.kfx.controls.bettertable.DefaultCellFactory
import de.flapdoodle.kfx.controls.fields.DefaultFieldFactoryLookup
import java.util.*

object CellFactories {
    val fieldFactoryLookup = DefaultFieldFactoryLookup(
        locale = Locale.getDefault(),
        converterFactory = Converters.defaultValidatorFactory
    )

    fun <T : Any> defaultCellFactory() = DefaultCellFactory<T>(
        fieldFactoryLookup = fieldFactoryLookup
    )
}