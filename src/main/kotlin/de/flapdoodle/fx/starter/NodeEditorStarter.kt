package de.flapdoodle.fx.starter

import de.flapdoodle.fx.clone.GraphEditorView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import tornadofx.App
import tornadofx.FX
import tornadofx.Stylesheet
import tornadofx.importStylesheet

class NodeEditorStarter : App(NodeEditorView::class, Style::class) {

    init {
        FX.layoutDebuggerShortcut =
            KeyCodeCombination(KeyCode.J, KeyCodeCombination.ALT_ANY, KeyCodeCombination.META_ANY)
    }

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            tornadofx.launch<NodeEditorStarter>(*args)
        }
    }

    class Style : Stylesheet() {
        init {
            importStylesheet(GraphEditorView.getStyleResource())
        }
    }
}