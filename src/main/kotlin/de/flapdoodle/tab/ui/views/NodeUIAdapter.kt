package de.flapdoodle.tab.ui.views

import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.tab.ui.resources.Labels
import javafx.scene.control.Button
import javafx.scene.layout.StackPane

abstract class NodeUIAdapter(): StackPane() {
    abstract fun update(node: de.flapdoodle.tab.model.Node)

    protected val context = Labels.with(ConstantUIAdapter::class)

    protected fun button(key: String, fallback: String): Button {
        val button = Button(context.text(key, fallback))
        button.cssClassName("button-"+key)
        return button
    }
}