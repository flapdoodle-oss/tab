package de.flapdoodle.tab.data.nodes

import de.flapdoodle.tab.data.NamedColumn

interface HasColumns {
  fun columns(): List<NamedColumn<out Any>>
}