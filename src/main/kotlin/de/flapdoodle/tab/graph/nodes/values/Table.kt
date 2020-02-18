package de.flapdoodle.tab.graph.nodes.values

import de.flapdoodle.tab.graph.nodes.NodeFactory
import javafx.event.EventTarget
import javafx.scene.Parent
import javafx.scene.control.TableView
import tornadofx.*

class Table : Fragment(), NodeFactory<TableView<Dummy>> {
  val list = listOf(
      Dummy("Klaus", 17),
      Dummy(null, 19),
      Dummy(null, null)
  ).toObservable()

  override val root = tableview(list) {
    isEditable = true
    column("name", Dummy::nameProperty).makeEditable()
    column("age", Dummy::ageProperty).makeEditable()
  }

  override fun newInstance(): TableView<Dummy> {
    return TableView<Dummy>().apply {
      this.items = list

      isEditable = true
      column("name", Dummy::nameProperty).makeEditable()
      column("age", Dummy::ageProperty).makeEditable()
    }
  }
}