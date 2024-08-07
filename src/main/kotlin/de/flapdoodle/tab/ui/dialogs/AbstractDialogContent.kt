package de.flapdoodle.tab.ui.dialogs

import de.flapdoodle.kfx.dialogs.DialogContent
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import de.flapdoodle.tab.ui.resources.Labels

abstract class AbstractDialogContent<T: Any> : WeightGridPane(), DialogContent<T> {
    override fun title(): String {
        return Labels.text(this::class,"title", requireNotNull(this::class.simpleName) {"${this::class} has no simpleName"})
    }

    override fun abort(): T? {
        return null
    }

    override fun enter() {
        
    }
}