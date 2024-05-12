package de.flapdoodle.tab.ui.resources

object Messages {
    fun message(key: String, vararg args: Any): String {
        return ResourceBundles.messages().message(key, *args) ?: "$key ??"
    }
}