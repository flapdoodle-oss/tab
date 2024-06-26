package de.flapdoodle.tab.ui.views.calculations

import de.flapdoodle.kfx.controls.labels.ValidatedLabel
import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.converters.ValueOrError
import de.flapdoodle.kfx.layout.grid.TableCell
import de.flapdoodle.kfx.layout.grid.WeightGridTable
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.data.SingleValue
import de.flapdoodle.tab.types.Unknown
import de.flapdoodle.tab.ui.resources.Labels
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
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
            WeightGridTable.Column(weight = 0.0, cellFactory = {
                TableCell.with(Labels.label(it.name.shortest()), { value -> value.name.shortest() }, Label::setText)
            }),
            WeightGridTable.Column(weight = 0.0, cellFactory = {
                TableCell(Labels.label("="))
            }),
            WeightGridTable.Column(weight = 1.0, cellFactory = { typedlabelCell(it) as TableCell<SingleValue<out Any>, out Node> })
        ),
    )

    init {
        children.add(valuesPanel)
    }

    private fun <T : Any> typedlabelCell(value: SingleValue<T>): TableCell<SingleValue<T>, ValidatedLabel<T>> {
        return TableCell(typedlabel(value)) { t, v -> t.set(v.value) }
    }

    private fun <T : Any> typedlabel(value: SingleValue<T>): ValidatedLabel<T> {
        val converter: ValidatingConverter<T> = if (value.valueType.isAssignable(TypeInfo.of(Unknown::class.java))) {
            object : ValidatingConverter<T> {
                override fun fromString(value: String?): ValueOrError<T> {
                    throw IllegalArgumentException("not implemented")
                }

                override fun toString(value: T): String {
                    return value.toString()
                }
            }
        } else {
            de.flapdoodle.tab.ui.Converters.validatingConverter(value.valueType)
        }
        return ValidatedLabel(converter).apply {
            set(value.value)
        }
    }

    fun update(node: de.flapdoodle.tab.model.Node.Calculated<K>) {
        valuesModel.value = node.values.values
    }
}