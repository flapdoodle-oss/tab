package de.flapdoodle.tab.ui.views.csv

import de.flapdoodle.kfx.dialogs.DialogContent
import de.flapdoodle.kfx.layout.grid.GridPane
import de.flapdoodle.tab.ui.resources.Labels

abstract class AbstractCsvDialogStep<T: Any>: GridPane(), DialogContent<T> {
    final override fun title(): String {
        return Labels.text(this::class,"title", requireNotNull(this::class.simpleName) {"${this::class} has no simpleName"})
    }
}