package de.flapdoodle.tab

import de.flapdoodle.kfx.usecase.tab2.Tab2
import javafx.application.Application

object Tab2Sampler {
    @JvmStatic
    fun main(args: Array<String>) {
        Application.launch(Tab2::class.java, *args)
    }
}