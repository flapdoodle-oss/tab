package de.flapdoodle.tab.ui.resources

import javafx.scene.control.Label
import javafx.scene.control.PopupControl.USE_PREF_SIZE
import kotlin.reflect.KClass

object Labels {
    fun label(context: KClass<out Any>, key: String, fallback: String): Label {
        return Label(text(context,key,fallback)).apply { minWidth = USE_PREF_SIZE }
    }

    fun text(context: KClass<out Any>, key: String, fallback: String): String {
        val prefix = requireNotNull(context.qualifiedName) { "qualifiedName is null for $context" }
        val shortName = requireNotNull(context.simpleName) { "simpleName is null for $context" }

        return ResourceBundles.labels()
            .message(listOf("$shortName.$key", "$prefix.$key",key)) ?: fallback
    }

    fun with(context: KClass<out Any>) = WithContext(context)

    class WithContext(val context: KClass<out Any>) {
        fun text(key: String, fallback: String): String = text(context, key, fallback)
        fun label(key: String, fallback: String): Label = label(context, key, fallback)
    }
}