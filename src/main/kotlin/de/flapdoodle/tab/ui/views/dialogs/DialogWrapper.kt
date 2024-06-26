package de.flapdoodle.tab.ui.views.dialogs

import de.flapdoodle.tab.ui.resources.Labels
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog

class DialogWrapper<T: Any>(
    factory: () -> DialogContent<T>
) : Dialog<T>() {

    init {
        val content = factory()

        title = Labels.text(content::class,"title", requireNotNull(content::class.simpleName) {"${content::class} has no simpleName"})
        
        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
        dialogPane.content = content

        val okButton = dialogPane.lookupButton(ButtonType.OK)
        okButton.disableProperty().bind(content.isValidProperty())

        setResultConverter { dialogButton: ButtonType? ->
            if (dialogButton?.buttonData == ButtonData.OK_DONE) {
                content.result()
            } else
                content.abort()
        }
    }

    companion object {
        fun <T: Any> open(factory: () -> DialogContent<T>): T? {
            val dialog = DialogWrapper(factory)
            return dialog.showAndWait().orElse(null)
        }
    }
}