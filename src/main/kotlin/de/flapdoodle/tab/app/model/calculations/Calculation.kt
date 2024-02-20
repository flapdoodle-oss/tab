package de.flapdoodle.tab.app.model.calculations

sealed class Calculation {
    abstract fun variableNames(): Set<String>

    // columnId or valueId as destination??
}