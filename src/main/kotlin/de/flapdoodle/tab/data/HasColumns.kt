package de.flapdoodle.tab.data

import de.flapdoodle.tab.types.Id

interface HasColumns {
  fun id(): Id<out HasColumns>
  fun columns(): List<NamedColumn<out Any>>
}