package de.flapdoodle.tab.graph.events3

import tornadofx.*

interface HasUIComponent<T: UIComponent> {
  fun uiComponent(): T
}