package de.flapdoodle.tab.ui.resources

import de.flapdoodle.kfx.i18n.I18N
import java.util.*

object ResourceBundles {
    private val labels = I18N.resourceBundle(Locale.getDefault(), "labels")

    private val enumTypesResourceBundle = I18N.resourceBundle(Locale.getDefault(), "enumTypes")
    private val typesResourceBundle = I18N.resourceBundle(Locale.getDefault(), "valueTypes")
    private val indexTypesResourceBundle = I18N.resourceBundle(Locale.getDefault(), "indexTypes")

    fun labels() = labels

    fun enumTypes() = enumTypesResourceBundle
    fun valueTypes() = typesResourceBundle
    fun indexTypes() = indexTypesResourceBundle

}