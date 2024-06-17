package de.flapdoodle.tab.ui.views.common

import de.flapdoodle.kfx.colors.HashedColors
import javafx.beans.property.ObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.ColorPicker

class HashedColorPicker(
    private val nameProperty: ObjectProperty<String>
) : ColorPicker() {
    var useHashedColor = true
    init {
        customColors.addAll(HashedColors.colors())

        onAction = EventHandler {
            useHashedColor = false
        }

        nameProperty.addListener { _, _, newValue ->
            if (useHashedColor) {
                value = HashedColors.hashedColor(newValue ?: "")
            }
        }
        
        value = HashedColors.hashedColor(nameProperty.value ?: "")
    }
}