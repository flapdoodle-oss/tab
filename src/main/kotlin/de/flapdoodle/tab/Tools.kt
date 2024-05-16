package de.flapdoodle.tab

import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.extensions.change
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.Position
import de.flapdoodle.tab.model.Tab2Model
import de.flapdoodle.tab.ui.Tab2ModelAdapter
import de.flapdoodle.tab.ui.commands.Command
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.views.dialogs.NewCalculationDialog
import de.flapdoodle.tab.ui.views.dialogs.NewTableDialog
import de.flapdoodle.tab.ui.views.dialogs.NewValuesDialog
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.Separator
import javafx.scene.control.ToolBar

class Tools(
    val adapter: (Command) -> Unit,
    val modelChangeAdapter: ((Tab2Model) -> Tab2Model) -> Unit,
    val selectedVertex: ReadOnlyObjectProperty<Id<out Node>>,
    val selectedEdge: ReadOnlyObjectProperty<Tab2ModelAdapter.Output2Input>
) : ToolBar() {
    private val context = Labels.with(Tools::class)

    init {
//        bindCss("tools")

        items.addAll(
            button("Values").also { button ->
                button.onAction = EventHandler {
                    val node = NewValuesDialog.open()
                    if (node != null) {
                        adapter(Command.AskForPosition(onSuccess = { pos ->
                            modelChangeAdapter { it.addNode(node.copy(position = Position(pos.x, pos.y))) }
                        }))
                    }
                }
            },
            button("Table").also { button ->
                button.onAction = EventHandler {
                    val node = NewTableDialog.open()
                    if (node != null) {
                        val changed = node // fakeDummy(node)
                        adapter(Command.AskForPosition(onSuccess = { pos ->
                            modelChangeAdapter { it.addNode(changed.copy(position = Position(pos.x, pos.y))) }
                        }))
                    }
                }
            },
            button("Calculated").also { button ->
                button.onAction = EventHandler {
                    val node = NewCalculationDialog.open()
                    if (node != null) {
                        adapter(Command.AskForPosition(onSuccess = { pos ->
                            modelChangeAdapter { it.addNode(node.copy(position = Position(pos.x, pos.y))) }
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
                    modelChangeAdapter { it.removeNode(vertexId) }
                }
            },
            button("deleteEdge","delete Connection").also { button ->
                button.visibleProperty().bind(selectedEdge.map { it != null })
                button.managedProperty().bind(button.visibleProperty())
                button.onAction = EventHandler {
                    val edge = selectedEdge.value
                    modelChangeAdapter { it.disconnect(edge.id, edge.input, edge.source) }
                }
            },
            )
    }

    private fun button(fallback: String): Button {
        return Button(context.text(fallback.lowercase(), fallback))
    }

    private fun button(key: String, fallback: String): Button {
        return Button(context.text(key, fallback))
    }
}