package de.flapdoodle.tab.bindings

import javafx.beans.property.SimpleObjectProperty
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ObjectPropertiesTest {

  @Test
  fun `sync must work both ways without spinning into a loop`() {
    val a = SimpleObjectProperty("foo")
    val b = SimpleObjectProperty(12)

    val registration = ObjectProperties.bidirectionalMappedSync(a, b, Converter(
        to = { it?.length },
        from = { it.let { "x".repeat(it ?: 0)} }
    ))

    a.value = "ABC"

    assertThat(b.value).isEqualTo(3)

    b.value = 12

    assertThat(a.value).isEqualTo("xxxxxxxxxxxx")

    registration.remove()

    b.value = 3

    assertThat(a.value).isEqualTo("xxxxxxxxxxxx")
  }
}