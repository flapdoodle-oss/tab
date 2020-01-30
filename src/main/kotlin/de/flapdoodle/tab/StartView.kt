package de.flapdoodle.tab

import tornadofx.*

class StartView : View("My View") {
  override val root = borderpane {
    center = label {
      text="Tab"
    }
  }
}
