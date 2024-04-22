package de.flapdoodle.tab

import javafx.application.Application

object TabSampler {
    @JvmStatic
    fun main(args: Array<String>) {
        Application.launch(Tab::class.java, *args)
    }
}