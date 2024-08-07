package de.flapdoodle.tab.ui.resources

import de.flapdoodle.kfx.controls.labels.ValidatedLabel
import de.flapdoodle.kfx.i18n.I18NEnumStringConverter
import de.flapdoodle.kfx.layout.grid.TableCell
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.PopupControl.USE_PREF_SIZE
import kotlin.reflect.KClass

object Labels {
    fun label(text: String): Label {
        return Label(text).apply { minWidth = USE_PREF_SIZE }
    }

    fun label(property: ObservableValue<String>): Node {
        return Label().apply {
            textProperty().bind(property)
        }
    }

    fun label(context: KClass<out Any>, key: String, fallback: String): Label {
        return label(text(context, key, fallback))
    }

    fun name(context: KClass<out Any>): Label {
        return label(context, "name", "Name")
    }

    fun text(context: KClass<out Any>, key: String, fallback: String): String {
        val prefix = requireNotNull(context.qualifiedName) { "qualifiedName is null for $context" }
        val shortName = requireNotNull(context.simpleName) { "simpleName is null for $context" }

        return ResourceBundles.labels()
            .message(listOf("$shortName.$key", "$prefix.$key", key)) ?: fallback
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

    fun <T: Any, E : Enum<E>> enumTableCell(initialValue: T, type: KClass<E>, mapper: (T) -> E?): TableCell<T, ValidatedLabel<E>> {
        return TableCell.initializedWith(initialValue)
            .node(ValidatedLabel(I18NEnumStringConverter(ResourceBundles.enumTypes(), type)))
            .map(mapper)
            .updateWith(ValidatedLabel<E>::set)
    }
}