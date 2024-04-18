package de.flapdoodle.tab.app.io.file

data class FileCalculation(
    //val indexType: KClass<K>,
    val name: String,
    val expression: String,
    val id: String,
    val aggregation: Aggregation? = null,
    val tabular: Tabular? = null
) {
    init {
        val aggregationSet = aggregation != null
        val tabularSet = tabular != null
        require(aggregationSet || tabularSet) { "aggregation and tabular not set" }
        require(!(aggregationSet && tabularSet)) { "aggregation and tabular set" }
    }

    data class Aggregation(
//        private val indexType: KClass<K>,
//        private val name: String,
//        private val formula: Formula,
        val destination: String,
    )

    data class Tabular(
//        private val indexType: KClass<K>,
//        private val name: String,
//        private val formula: Formula,
        val destination: String,
    )
}