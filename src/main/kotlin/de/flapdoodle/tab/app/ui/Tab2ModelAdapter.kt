package de.flapdoodle.tab.app.ui

import de.flapdoodle.kfx.controls.grapheditor.GraphEditor
import de.flapdoodle.kfx.controls.grapheditor.events.EventListener
import de.flapdoodle.kfx.extensions.unsubscribeOnDetach
import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.tab.app.model.Tab2Model
import de.flapdoodle.tab.app.ui.commands.Command
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.scene.layout.AnchorPane

class Tab2ModelAdapter(
    model: ReadOnlyObjectProperty<Tab2Model>
) : AnchorPane() {
    private val graphEditor =
        GraphEditor(eventListener = EventListener { graphEditor, event -> false }).withAnchors(all = 10.0)

    init {
        children.add(graphEditor)
        apply(Action.syncActions(Tab2Model(), model.value))

        unsubscribeOnDetach {
            model.subscribe { old, current ->
                apply(Action.syncActions(old, current))
            }
        }
    }

    fun execute(command: Command) {
        val mapped = when (command) {
            is Command.Abort -> de.flapdoodle.kfx.controls.grapheditor.commands.Command.Abort()
            is Command.AskForPosition -> de.flapdoodle.kfx.controls.grapheditor.commands.Command.AskForPosition(command.onSuccess)
//            is Command.FindVertex -> {
//                val pos = vertexMapping[command.vertex]!!.vertex.layoutPosition
//                de.flapdoodle.kfx.controls.grapheditor.commands.Command.PanTo(pos,command.onSuccess)
//            }
        }
        graphEditor.execute(mapped)
    }


    private fun apply(action: List<Action>) {
        action.forEach { action ->
            println("action: $action")

            when (action) {
                is Action.AddNode -> {

                }

                is Action.RemoveNode -> {

                }

                else -> {
                    println("not implemented: $action")
                }
            }
        }
    }
}