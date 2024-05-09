package de.flapdoodle.tab

import de.flapdoodle.tab.model.Position
import de.flapdoodle.tab.model.Tab2Model
import de.flapdoodle.tab.ui.commands.Command
import de.flapdoodle.tab.ui.views.dialogs.NewCalculationDialog
import de.flapdoodle.tab.ui.views.dialogs.NewTableDialog
import de.flapdoodle.tab.ui.views.dialogs.NewValuesDialog
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.layout.FlowPane

class Toolbar(
    val adapter: (Command) -> Unit,
    val modelChangeAdapter: ((Tab2Model) -> Tab2Model) -> Unit
) : FlowPane() {
    init {
        children.addAll(
            Button("Values").also { button ->
                button.onAction = EventHandler {
                    val node = NewValuesDialog.open()
                    if (node != null) {
                        adapter(Command.AskForPosition(onSuccess = { pos ->
                            modelChangeAdapter { it.addNode(node.copy(position = Position(pos.x, pos.y))) }
                        }))
                    }
                }
            },
            Button("Table").also { button ->
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
            Button("Calculated").also { button ->
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
}