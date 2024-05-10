package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.calculations.interpolation.InterpolationType
import de.flapdoodle.tab.model.data.Column
import javafx.scene.control.*
import javafx.scene.control.ButtonBar.ButtonData
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import kotlin.reflect.KClass

class NewColumnDialog<K: Comparable<K>>(
    val indexType: TypeInfo<in K>
) : Dialog<Column<K, out Any>>() {

    private val name = Label("Name")
    private val nameField = TextField()
    private val type = Label("Type")
    private val typeField = ChoiceBox<KClass<out Any>>().apply {
        items.addAll(Int::class, Double::class, BigInteger::class, BigDecimal::class, String::class, LocalDate::class)
        value = Int::class
    }

    private val interpolation = Label("Interpolation")
    private val interpolationField = ChoiceBox<InterpolationType>().apply {
        items.addAll(InterpolationType.values())
        value = InterpolationType.Linear
    }

    init {
        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
        dialogPane.content = WeightGridPane().apply {
            horizontalSpaceProperty().value = 10.0
            verticalSpaceProperty().value = 10.0

            setColumnWeight(0, 1.0)
            setColumnWeight(1, 3.0)

            WeightGridPane.setPosition(name, 0, 0)
            WeightGridPane.setPosition(nameField, 1, 0)
            WeightGridPane.setPosition(type, 0, 1)
            WeightGridPane.setPosition(typeField, 1, 1)
            WeightGridPane.setPosition(interpolation, 0, 2)
            WeightGridPane.setPosition(interpolationField, 1, 2)

            children.addAll(name, nameField, type, typeField, interpolation, interpolationField)
        }

        setResultConverter { dialogButton: ButtonType? ->
            if (dialogButton?.buttonData == ButtonData.OK_DONE) {
                Column(
                    name = nameField.text,
                    indexType = indexType,
                    valueType = TypeInfo.of(typeField.selectionModel.selectedItem.javaObjectType),
                    interpolationType = interpolationField.selectionModel.selectedItem ?: InterpolationType.Linear
                )
            } else null
        }
    }

    companion object {
        fun <K: Comparable<K>> open(indexType: TypeInfo<in K>): Column<K, out Any>? {
            val dialog = NewColumnDialog(indexType)
            return dialog.showAndWait().orElse(null)
        }
    }
}