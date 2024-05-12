package de.flapdoodle.tab.ui.resources

import javafx.scene.control.Label
import kotlin.reflect.KClass

object Labels {
    fun translated(context: KClass<out Any>, key: String, fallback: String): Label {
        val prefix = requireNotNull(context.qualifiedName) { "qualifiedName is null for $context" }
        val shortName = requireNotNull(context.simpleName) { "simpleName is null for $context" }

        return Label(
            ResourceBundles.labels().message("$shortName.$key")
                ?: ResourceBundles.labels().message("$prefix.$key")
                ?: fallback
        )
    }
}