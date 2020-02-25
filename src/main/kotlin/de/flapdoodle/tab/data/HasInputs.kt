package de.flapdoodle.tab.data

import de.flapdoodle.tab.data.values.Variable

interface HasInputs {
  fun variables(): Set<Variable<out Any>>
}