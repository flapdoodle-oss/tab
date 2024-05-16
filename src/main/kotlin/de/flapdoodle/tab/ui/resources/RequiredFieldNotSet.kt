package de.flapdoodle.tab.ui.resources

import de.flapdoodle.kfx.i18n.I18N

class RequiredFieldNotSet(
    message: String,
) : Exception(message) {

    override fun getLocalizedMessage(): String {
        return I18N.exceptionMessage(ResourceBundles.exceptions(), RequiredFieldNotSet::class, "notSet")
            ?: super.getLocalizedMessage()
    }
}