package de.flapdoodle.tab

import de.flapdoodle.tab.styles.TabStyle
import tornadofx.*

class Tab : App(StartView::class, TabStyle::class) {

  companion object {
    @JvmStatic
    fun main(vararg args: String) {
      launch<Tab>(*args)
    }
  }
}