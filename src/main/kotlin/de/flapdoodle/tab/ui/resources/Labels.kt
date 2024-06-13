package de.flapdoodle.tab.ui.resources

import de.flapdoodle.kfx.layout.grid.TableCell
import javafx.scene.control.Label
import javafx.scene.control.PopupControl.USE_PREF_SIZE
import kotlin.reflect.KClass

object Labels {
    fun label(text: String): Label {
        return Label(text).apply { minWidth = USE_PREF_SIZE }
    }
    
    fun label(context: KClass<out Any>, key: String, fallback: String): Label {
        return label(text(context,key,fallback))
    }

    fun name(context: KClass<out Any>): Label {
        return label(context, "name", "Name")
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

    private fun <T> tableCell(mapper: (T) -> String): TableCell<T, Label> {
        return TableCell.with(Label(""))
            .map(mapper)
            .updateWith { label, s -> label.text = s }
    }

    fun <T> tableCell(initialValue: T, mapper: (T) -> String): TableCell<T, Label> {
        return tableCell(mapper).initializedWith(initialValue)
    }
}