package de.flapdoodle.tab.data.nodes

import de.flapdoodle.tab.data.values.Variable

interface HasInputs {
  fun variables(): Set<Variable<out Any>>
}