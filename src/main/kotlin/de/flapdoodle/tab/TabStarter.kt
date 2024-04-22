package de.flapdoodle.tab

import javafx.application.Application

object TabStarter {
    @JvmStatic
    fun main(args: Array<String>) {
        Application.launch(Tab::class.java, *args)
    }
}