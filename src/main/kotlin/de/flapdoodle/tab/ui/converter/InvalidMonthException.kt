package de.flapdoodle.tab.ui.converter

class InvalidMonthException(val month: String, val months: Set<String>) :
    RuntimeException(
        "could not find $month in $months"
    ) {
}