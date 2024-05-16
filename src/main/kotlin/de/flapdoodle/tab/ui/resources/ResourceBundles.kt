package de.flapdoodle.tab.ui.resources

import de.flapdoodle.kfx.i18n.I18N
import java.util.*

object ResourceBundles {
    private val labels = I18N.resourceBundle(Locale.getDefault(), "de.flapdoodle.tab.labels")
    private val messages = I18N.resourceBundle(Locale.getDefault(), "de.flapdoodle.tab.messages")

    private val enumTypesResourceBundle = I18N.resourceBundle(Locale.getDefault(), "de.flapdoodle.tab.enumTypes")
    private val typesResourceBundle = I18N.resourceBundle(Locale.getDefault(), "de.flapdoodle.tab.valueTypes")
    private val indexTypesResourceBundle = I18N.resourceBundle(Locale.getDefault(), "de.flapdoodle.tab.indexTypes")

    private val exceptions = I18N.resourceBundle(Locale.getDefault(), "de.flapdoodle.tab.exceptions")

    fun labels() = labels
    fun messages() = messages

    fun enumTypes() = enumTypesResourceBundle
    fun valueTypes() = typesResourceBundle
    fun indexTypes() = indexTypesResourceBundle

    fun exceptions() = exceptions
}