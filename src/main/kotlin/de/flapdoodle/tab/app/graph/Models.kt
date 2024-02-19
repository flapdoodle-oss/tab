package de.flapdoodle.kfx.usecase.tab2.graph

import de.flapdoodle.kfx.controls.grapheditor.slots.Position
import de.flapdoodle.kfx.controls.grapheditor.slots.Slot
import de.flapdoodle.kfx.controls.graphmodeleditor.model.Edge
import de.flapdoodle.kfx.controls.graphmodeleditor.model.Model
import de.flapdoodle.kfx.controls.graphmodeleditor.model.Vertex

object Models {
  fun emptyModel() = Model<String>()

  fun testModel(): Model<String> {
    val a = Vertex("a", "A", listOf(Slot("X-->", Slot.Mode.OUT, Position.RIGHT)))
    val b = Vertex("b", "B", listOf(Slot("-->X", Slot.Mode.IN, Position.LEFT)))
    return emptyModel()
      .add(a)
      .add(b)
      .add(Edge(a.id, a.slots[0].id, b.id, b.slots[0].id))
  }
}