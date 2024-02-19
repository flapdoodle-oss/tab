package de.flapdoodle.tab

import tornadofx.launch

object TabSampler {
    @JvmStatic
    fun main(vararg args: String) {
        launch<Tab>(*args)
    }
}