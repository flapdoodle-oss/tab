package de.flapdoodle.tab.extensions

object Exceptions {
  fun <T> rethrowWithMessage(message: String, block: () -> T): T {
    return rethrowWithMessage({ message }, block)
  }

  @Suppress("TooGenericExceptionCaught", "TooGenericExceptionThrown")
  fun <T> rethrowWithMessage(message: () -> String, block: () -> T): T {
    try {
      return block()
    } catch (ex: RuntimeException) {
      when (ex) {
        is IllegalArgumentException -> throw IllegalArgumentException(message(), ex)
        is IllegalStateException -> throw IllegalStateException(message(), ex)
        else -> throw RuntimeException(message(), ex)
      }
    }
  }

  inline fun <T, reified EX> returnOnException(fallback: T?, block: () -> T?): T? {
    try {
      return block()
    } catch (ex: Exception) {
      if (ex is EX) {
        return fallback
      }
      throw ex
    }
  }
}