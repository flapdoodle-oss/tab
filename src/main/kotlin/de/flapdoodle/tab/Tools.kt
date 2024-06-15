package de.flapdoodle.tab

import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.*
import de.flapdoodle.tab.ui.Tab2ModelAdapter
import de.flapdoodle.tab.ui.commands.Command
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.views.dialogs.NewCalculated
import de.flapdoodle.tab.ui.views.dialogs.NewTable
import de.flapdoodle.tab.ui.views.dialogs.NewValues
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.Separator
import javafx.scene.control.ToolBar

class Tools(
    val adapter: (Command) -> Unit,
    val modelChangeAdapter: ((Model) -> Model) -> Unit,
    val selectedVertex: ReadOnlyObjectProperty<Id<out Node>>,
    val selectedEdge: ReadOnlyObjectProperty<Tab2ModelAdapter.Output2Input>
) : ToolBar() {
    private val context = Labels.with(Tools::class)

    init {
        cssClassName("tools")
//        bindCss("tools")

        items.addAll(
            button("Values").also { button ->
                button.onAction = EventHandler {
                    val node = NewValues.open()
                    if (node != null) {
                        adapter(Command.AskForPosition(onSuccess = { pos ->
                            modelChangeAdapter { it.apply(Change.AddNode(node.copy(position = Position(pos.x, pos.y)))) }
                        }))
                    }
                }
            },
            button("Table").also { button ->
                button.onAction = EventHandler {
                    val node = NewTable.open()
                    if (node != null) {
                        val changed = node // fakeDummy(node)
                        adapter(Command.AskForPosition(onSuccess = { pos ->
                            modelChangeAdapter { it.apply(Change.AddNode(changed.copy(position = Position(pos.x, pos.y)))) }
                        }))
                    }
                }
            },
            button("Calculated").also { button ->
                button.onAction = EventHandler {
                    val node = NewCalculated.open()
                    if (node != null) {
                        adapter(Command.AskForPosition(onSuccess = { pos ->
                            modelChangeAdapter { it.apply(Change.AddNode(node.copy(position = Position(pos.x, pos.y)))) }
                        }))
                    }
                }
            },
            Separator(),
            button("deleteVertex", "delete Node").also { button ->
                button.visibleProperty().bind(selectedVertex.map { it != null })
                button.managedProperty().bind(button.visibleProperty())
                button.onAction = EventHandler {
                    val vertexId = selectedVertex.value
                    modelChangeAdapter { it.apply(Change.RemoveNode(vertexId)) }
                }
            },
            button("deleteEdge","delete Connection").also { button ->
                button.visibleProperty().bind(selectedEdge.map { it != null })
                button.managedProperty().bind(button.visibleProperty())
                button.onAction = EventHandler {
                    val edge = selectedEdge.value
                    modelChangeAdapter { it.apply(Change.Disconnect(edge.id, edge.input, edge.source)) }
                }
            },
            )
    }

    private fun button(fallback: String): Button {
        return button(fallback.lowercase(), fallback)
    }

    private fun button(key: String, fallback: String): Button {
        val button = Button(context.text(key, fallback))
        button.cssClassName("button-"+key)
        return button
    }
}