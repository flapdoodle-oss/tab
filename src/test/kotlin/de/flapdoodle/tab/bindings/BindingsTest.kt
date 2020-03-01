package de.flapdoodle.tab.bindings

import javafx.beans.property.SimpleStringProperty
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class BindingsTest {

  @Test
  fun `bind must propagate`() {
    var lastValueFromChangeListener: String? = null

    val src = SimpleStringProperty("start")
    val mapped = src.mapNullable { ">$it<" }
    mapped.addListener { observable, oldValue, newValue ->
      lastValueFromChangeListener = newValue
    }

    assertThat(mapped.value).isEqualTo(">start<")
    assertThat(lastValueFromChangeListener).isNull()

    src.value = "new"
    assertThat(lastValueFromChangeListener).isEqualTo(">new<")
  }

  @Test
  fun `bind must propagate two levels`() {
    var lastValueFromChangeListener: String? = null

    val src = SimpleStringProperty("start")
    val mapped = src.mapNullable { ">$it<" }.mapNullable { "[$it]" }

    mapped.addListener { observable, oldValue, newValue ->
      lastValueFromChangeListener = newValue
    }

    assertThat(mapped.value).isEqualTo("[>start<]")
    assertThat(lastValueFromChangeListener).isNull()

    src.value = "new"
    assertThat(lastValueFromChangeListener).isEqualTo("[>new<]")
  }
}