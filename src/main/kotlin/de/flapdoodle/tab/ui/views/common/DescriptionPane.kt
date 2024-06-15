package de.flapdoodle.tab.ui.views.common

import de.flapdoodle.kfx.extensions.bindCss
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.StackPane

class DescriptionPane(
    description: String?
) : StackPane() {
    private val model = SimpleObjectProperty(description)
    private val label = Label()

    init {
        bindCss("description")
        label.textProperty().bind(model)
        label.isWrapText = true

        setAlignment(label, Pos.CENTER_LEFT)
        children.add(label)

        maxHeight = USE_PREF_SIZE
        isManaged = !description.isNullOrEmpty()
        isVisible = isManaged
    }

    fun update(description: String?) {
        model.value = description
        isManaged = !description.isNullOrEmpty()
        isVisible = isManaged
    }
}