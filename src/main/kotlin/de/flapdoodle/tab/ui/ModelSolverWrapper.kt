package de.flapdoodle.tab.ui

import de.flapdoodle.tab.model.Tab2Model
import de.flapdoodle.tab.model.graph.Solver
import de.flapdoodle.tab.ui.events.ModelEvent
import de.flapdoodle.tab.ui.events.ModelEventListener
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty

class ModelSolverWrapper(initalModel: Tab2Model = Tab2Model()) {
    private val model = SimpleObjectProperty(Solver.solve(initalModel))

    private var history = emptyList<Tab2Model>()
    private var offset = 0

    internal fun changeModel(change: (Tab2Model) -> Tab2Model) {
        val changed = change(model.value)
        val solved = Solver.solve(changed)
        history = listOf(solved) + history.subList(offset, history.size)
        offset = 0
        model.value = solved
    }

    internal fun replaceModel(change: (Tab2Model) -> Tab2Model) {
        val changed = change(model.value)
        val solved = Solver.solve(changed)
        history = listOf(solved) + history.subList(offset + 1, history.size)
        offset = 0
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
            is ModelEvent.VertexResized -> {
                changeModel { current ->
                    current.resizeTo(event.node, event.position, event.size)
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
    
    fun undo(): Boolean {
        return if ((offset+1) < history.size) {
            offset++
            model.value=history[offset]
            true
        } else {
            false
        }
    }

    fun redo(): Boolean {
        return if (offset>0) {
            offset--
            model.value=history[offset]
            true
        } else {
            false
        }
    }
}