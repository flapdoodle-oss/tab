package de.flapdoodle.tab.ui.dialogs

import de.flapdoodle.tab.ui.resources.Labels
import javafx.event.ActionEvent
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.stage.Window

class WizardWrapper<T: Any>(
    inital: T,
    private vararg val factories: WizardContentFactory<T>
) : Dialog<T>() {

    var current=0
    var currentState = inital
    var currentContent: WizardContent<T>

    init {
        require(factories.isNotEmpty()) { "no factories" }
        isResizable = true

        currentContent = factories[current].content(currentState)

        updateNavigation()

        setResultConverter { dialogButton: ButtonType? ->
            println("pressed: $dialogButton")

            if (dialogButton?.buttonData == ButtonData.OK_DONE) {
                currentContent.result()
            } else
                currentContent.abort()
        }

    }

    private fun updateNavigation() {
        title = Labels.text(current::class,"title", requireNotNull(current::class.simpleName) {"${current::class} has no simpleName"})

        dialogPane.buttonTypes.clear()
        dialogPane.buttonTypes.addAll(ButtonType.CANCEL)
        if (current > 0) {
            dialogPane.buttonTypes.addAll(ButtonType.PREVIOUS)
        }
        if (current < factories.size - 1) {
            dialogPane.buttonTypes.addAll(ButtonType.NEXT)
        } else {
            dialogPane.buttonTypes.addAll(ButtonType.OK)
        }

        dialogPane.content = currentContent

        val okButton = dialogPane.lookupButton(ButtonType.OK)
        val nextButton = dialogPane.lookupButton(ButtonType.NEXT)
        val prevButton = dialogPane.lookupButton(ButtonType.PREVIOUS)

        if (nextButton != null) {
            nextButton.disableProperty().bind(currentContent.isValidProperty())
            nextButton.addEventFilter(ActionEvent.ACTION) { event ->
                event.consume()
                
                current++
                val nextState = currentContent.result()
                requireNotNull(nextState) { "next state is null" }
                currentState = nextState
                currentContent = factories[current].content(currentState)
                updateNavigation()
            }
        } else {
            okButton.disableProperty().bind(currentContent.isValidProperty())
        }
    }

    companion object {

        fun <T: Any> open(inital: T, vararg factories: WizardContentFactory<T>): T? {
            val dialog = WizardWrapper(inital, *factories)
            return dialog.showAndWait()
                .orElse(null)
        }

        fun <T: Any> open(window: Window, inital: T, vararg factories: WizardContentFactory<T>): T? {
            val dialog = WizardWrapper(inital, *factories)
            dialog.initOwner(window)
            return dialog.showAndWait().orElse(null)
        }
    }

}