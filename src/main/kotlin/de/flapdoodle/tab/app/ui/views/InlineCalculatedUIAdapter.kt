package de.flapdoodle.tab.app.ui.views

import de.flapdoodle.kfx.controls.textfields.TypedLabel
import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.tab.app.model.Node
import de.flapdoodle.tab.app.model.data.SingleValue
import de.flapdoodle.tab.app.ui.ModelChangeListener
import de.flapdoodle.tab.app.ui.views.calculations.CalculationsPane
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Label

class InlineCalculatedUIAdapter<K: Comparable<K>>(
    node: Node.Calculated<K>,
    val modelChangeListener: ModelChangeListener
) : NodeUIAdapter() {
    val nodeId = node.id

    val calculationsPane = CalculationsPane(node, modelChangeListener)

    val valuesModel = SimpleObjectProperty(node.values.values)
    val valuesPanel = WeightGridTable(
        model = valuesModel,
        columns = listOf(
            WeightGridTable.Column(weight = 1.0, nodeFactory = { Label(it.name) }),
            WeightGridTable.Column(weight = 0.1, nodeFactory = { Label("=") }),
            WeightGridTable.Column(weight = 10.0, nodeFactory = { typedlabel(it) })),
    )

    init {
        children.add(calculationsPane)
        children.add(valuesPanel)
    }

    private fun <T: Any> typedlabel(value: SingleValue<T>): TypedLabel<T> {
        return TypedLabel(value.valueType).apply {
            set(value.value)
        }
    }

    override fun update(node: Node) {
        require(node is Node.Calculated<out Comparable<*>>) { "wrong type $node" }
        valuesModel.value = node.values.values
    }
}