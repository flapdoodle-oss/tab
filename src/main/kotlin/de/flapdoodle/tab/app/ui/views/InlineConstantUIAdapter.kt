package de.flapdoodle.tab.app.ui.views

import de.flapdoodle.kfx.controls.textfields.TypedTextField
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.change.ModelChange
import de.flapdoodle.tab.app.model.data.SingleValue
import de.flapdoodle.tab.app.model.data.SingleValueId
import de.flapdoodle.tab.app.model.data.SingleValues
import de.flapdoodle.tab.app.ui.ModelChangeListener
import javafx.scene.control.Label
import javafx.scene.control.TextField

class InlineConstantUIAdapter(
    node: Node.Constants,
    val modelChangeListener: ModelChangeListener
) : NodeUIAdapter() {
    val content = WeightGridPane().apply {
        setRowWeight(0,1.0)
        setRowWeight(1, 10.0)
    }
    val nodeId = node.id
    var singleValues: SingleValues = SingleValues()
    var rows = emptyList<Pair<SingleValueId, Pair<Label, TextField>>>()

    init {
        update(node.values)
        children.add(content)
    }

    private fun update(values: SingleValues) {
//        val change = Diff.between(singleValues.values, values.values, SingleValue<out Any>::id)
        val expectedRows = labelAndTextfields(nodeId, values, modelChangeListener)
        //val change = Diff.between(rows, expectedRows, Pair<SingleValueId, Pair<Label, TextField>>::first)
        rows.forEach { content.children.removeAll(it.second.first, it.second.second) }
        expectedRows.forEachIndexed { index, it ->
            WeightGridPane.setPosition(it.second.first,0,index)
            content.children.add(it.second.first)
            WeightGridPane.setPosition(it.second.second,1,index)
            content.children.add(it.second.second)
        }

        singleValues = values
    }

    private fun labelAndTextfields(id: Id<out Node.Constants>, values: SingleValues, modelChangeListener: ModelChangeListener): List<Pair<SingleValueId, Pair<Label, TextField>>> {
        return values.values.map {
            it.id to (Label(it.name) to textField(id, it, modelChangeListener))
        }
    }

    private fun <T: Any> textField(id: Id<out Node.Constants>, value: SingleValue<T>, modelChangeListener: ModelChangeListener): TypedTextField<T> {
        return TypedTextField(value.valueType).apply {
            set(value.value)
            prefWidth = 60.0
            valueProperty().addListener { observable, oldValue, newValue ->
                modelChangeListener.change(ModelChange.ChangeValue(id, value.id, newValue))
            }
        }
    }

    override fun update(node: Node) {
        require(node is Node.Constants) { "wrong type $node" }
        update(node.values)
    }
}