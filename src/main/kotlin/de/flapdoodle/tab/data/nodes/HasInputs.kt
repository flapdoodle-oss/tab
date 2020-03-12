package de.flapdoodle.tab.data.nodes

import de.flapdoodle.tab.data.values.Input

interface HasInputs {
  fun variables(): Set<Input<out Any>>
}