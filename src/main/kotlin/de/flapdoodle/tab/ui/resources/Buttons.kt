package de.flapdoodle.tab.ui.resources

import de.flapdoodle.kfx.extensions.cssClassName
import javafx.event.EventHandler
import javafx.scene.control.Button

object Buttons {

    fun button(context: Labels.WithContext, key: String, fallback: String): Button {
        val button = Button(context.text(key, fallback))
        button.cssClassName("button-"+key)
        return button
    }

    fun button(context: Labels.WithContext, key: String, fallback: String, onAction: () -> Unit): Button {
        val button = button(context,key,fallback)
        button.onAction = EventHandler {
            onAction()
        }
        return button
    }

    fun add(context: Labels.WithContext, onAction: () -> Unit): Button {
        return button(context,"add", "+", onAction)
    }

    fun change(context: Labels.WithContext): Button {
        return button(context,"change", "?")
    }

    fun delete(context: Labels.WithContext, onAction: () -> Unit): Button {
        return button(context,"delete", "-", onAction)
    }
}