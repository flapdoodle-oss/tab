package de.flapdoodle.fx.starter

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import tornadofx.App
import tornadofx.FX

class NodeEditorStarter : App(NodeEditorView::class) {

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
}