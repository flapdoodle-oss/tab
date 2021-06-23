package de.flapdoodle.fx.starter

import tornadofx.App

class NodeEditorStarter : App(NodeEditorView::class) {

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            tornadofx.launch<NodeEditorStarter>(*args)
        }
    }
}