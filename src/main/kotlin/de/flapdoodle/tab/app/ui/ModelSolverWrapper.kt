package de.flapdoodle.tab.app.ui

import de.flapdoodle.tab.app.model.Position
import de.flapdoodle.tab.app.model.Tab2Model
import de.flapdoodle.tab.app.model.graph.Solver
import de.flapdoodle.tab.app.ui.events.ModelEvent
import de.flapdoodle.tab.app.ui.events.ModelEventListener
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty

class ModelSolverWrapper(initalModel: Tab2Model = Tab2Model()) {
    private val model = SimpleObjectProperty(Solver.solve(initalModel))

    internal fun changeModel(change: (Tab2Model) -> Tab2Model) {
        val changed = change(model.value)
        val solved = Solver.solve(changed)
        model.value = solved
    }

    private fun validModelChange(change: (Tab2Model) -> Tab2Model): Boolean {
        try {
            change(model.value)
            return true
        } catch (ex: Exception) {
            return  false
        }
    }

    private val eventListener = ModelEventListener { event ->
        when (event) {
            is ModelEvent.ConnectTo -> {
                changeModel { current ->
                    current.connect(event.start, event.startDataOrInput, event.end, event.endDataOrInput)
                }
                true
            }
            is ModelEvent.TryToConnectTo -> {
                validModelChange { it.connect(event.start, event.startDataOrInput, event.end, event.endDataOrInput) }
            }
            is ModelEvent.VertexMoved -> {
                changeModel { current ->
                    current.moveTo(event.node, event.position)
                }
                true
            }
            else -> {
                true
            }
        }
    }

    private val modelChangeListener = ModelChangeListener { modelChange ->
        changeModel { old -> old.apply(modelChange) }
    }

    fun eventListener() = eventListener
    fun changeListener() = modelChangeListener
    fun model(): ReadOnlyObjectProperty<Tab2Model> = model
}