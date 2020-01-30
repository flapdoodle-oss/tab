package de.flapdoodle.tab

import tornadofx.*

class Tab : App(StartView::class) {

  companion object {
    @JvmStatic
    fun main(vararg args: String) {
      launch<Tab>(*args)
    }
  }
}