package de.flapdoodle.tab

import de.flapdoodle.tab.model.Position
import de.flapdoodle.tab.model.Tab2Model
import de.flapdoodle.tab.ui.commands.Command
import de.flapdoodle.tab.ui.resources.Labels
import de.flapdoodle.tab.ui.views.dialogs.NewCalculationDialog
import de.flapdoodle.tab.ui.views.dialogs.NewTableDialog
import de.flapdoodle.tab.ui.views.dialogs.NewValuesDialog
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.ToolBar
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox

class Tools(
    val adapter: (Command) -> Unit,
    val modelChangeAdapter: ((Tab2Model) -> Tab2Model) -> Unit
) : ToolBar() {
    private val context = Labels.with(Tools::class)

    init {
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
            }
        )
    }

    private fun button(fallback: String): Button {
        return Button(context.text(fallback.lowercase(), fallback))
    }
}