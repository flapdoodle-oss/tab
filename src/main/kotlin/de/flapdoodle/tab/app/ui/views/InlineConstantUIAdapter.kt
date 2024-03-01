package de.flapdoodle.tab.app.ui.views

import de.flapdoodle.kfx.collections.Diff
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.data.SingleValue
import de.flapdoodle.tab.app.model.data.SingleValueId
import de.flapdoodle.tab.app.model.data.SingleValues
import de.flapdoodle.tab.converter.Converters
import javafx.event.EventHandler
import javafx.scene.control.Label
import javafx.scene.control.TextField

class InlineConstantUIAdapter(
    node: Node.Constants
) : NodeUIAdapter() {
    val content = WeightGridPane().apply {
        setRowWeight(0,1.0)
        setRowWeight(1, 10.0)
    }
    var singleValues: SingleValues = SingleValues()
    var rows = emptyList<Pair<SingleValueId, Pair<Label, TextField>>>()

    init {
        update(node.values)
        children.add(content)
    }

    private fun update(values: SingleValues) {
//        val change = Diff.between(singleValues.values, values.values, SingleValue<out Any>::id)
        val expectedRows = labelAndTextfields(values)
        //val change = Diff.between(rows, expectedRows, Pair<SingleValueId, Pair<Label, TextField>>::first)
        rows.forEach { content.children.removeAll(it.second.first, it.second.second) }
        expectedRows.forEachIndexed { index, it ->
            content.children.add(it.second.first)
            WeightGridPane.setPosition(it.second.first,0,index)
            content.children.add(it.second.second)
            WeightGridPane.setPosition(it.second.second,1,index)
        }

        singleValues = values
    }

    private fun labelAndTextfields(values: SingleValues): List<Pair<SingleValueId, Pair<Label, TextField>>> {
        return values.values.map {
            it.id to (Label(it.name) to textField(it))
        }
    }

    private fun <T: Any> textField(value: SingleValue<T>): TextField {
        val converter = Converters.converterFor(value.valueType)
        return TextField(value.value?.let { converter.toString(it) }).apply {
            onAction = EventHandler {
                try {
                    val converted = converter.fromString(text)
                    println("converted: $converted")
                } catch (ex: RuntimeException) {
                    println("could not convert input: $ex")
                }
            }
//            textProperty().addListener { observable, oldValue, newValue ->
//                println("--> $newValue")
//            }
        }
    }

    override fun update(node: Node) {
        require(node is Node.Constants) { "wrong type $node" }
        update(node.values)
    }
}