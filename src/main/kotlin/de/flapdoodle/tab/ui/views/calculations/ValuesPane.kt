package de.flapdoodle.tab.ui.views.calculations

import de.flapdoodle.kfx.controls.fields.TypedLabel
import de.flapdoodle.kfx.controls.fields.ValidatedLabel
import de.flapdoodle.kfx.controls.fields.ValidatingTextField
import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.tab.model.data.SingleValue
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Label
import javafx.scene.layout.VBox

class ValuesPane<K : Comparable<K>>(
    node: de.flapdoodle.tab.model.Node.Calculated<K>
) : VBox() {
    private val valuesModel = SimpleObjectProperty(node.values.values)
    private val valuesPanel = WeightGridTable(
        model = valuesModel,
        indexOf = { it.id to it.valueType },
        columns = listOf(
            WeightGridTable.Column(weight = 0.0, nodeFactory = {
                val label = Label(it.name).apply {
                    minWidth = USE_PREF_SIZE
                }
                label to WeightGridTable.ChangeListener {
                    label.text = it.name
                }
            }),
            WeightGridTable.Column(weight = 0.0, nodeFactory = {
                Label("=").apply {
                    minWidth = USE_PREF_SIZE
                } to WeightGridTable.ChangeListener { }
            }),
            WeightGridTable.Column(weight = 10.0, nodeFactory = { typedlabelWithChangeListener(it) })
        ),
    )

    init {
        children.add(valuesPanel)
    }

    private fun <T : Any> typedlabelWithChangeListener(value: SingleValue<T>): Pair<javafx.scene.Node, WeightGridTable.ChangeListener<SingleValue<out Any>>> {
        val typedlabel = typedlabel(value)
        return typedlabel to WeightGridTable.ChangeListener {
            typedlabel.set(it.value as T?)
        }
    }

    private fun <T : Any> typedlabel(value: SingleValue<T>): ValidatedLabel<T> {
        val converter = de.flapdoodle.tab.ui.Converters.validatingConverter(value.valueType)
        return ValidatedLabel(converter).apply {
            set(value.value)
        }
    }

    fun update(node: de.flapdoodle.tab.model.Node.Calculated<K>) {
        valuesModel.value = node.values.values
    }
}