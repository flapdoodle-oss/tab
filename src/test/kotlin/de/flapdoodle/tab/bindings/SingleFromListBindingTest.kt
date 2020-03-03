package de.flapdoodle.tab.bindings

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SingleFromListBindingTest {
  @Test
  fun `map must keep reference to source to avoid GC`() {
    val src = FXCollections.observableArrayList("A", "B", "C")
    val mapped = src.gcAbleCopy().mapTo { it }

    System.gc()

    assertThat(mapped.value).containsExactly("A", "B", "C")
    src.setAll(listOf("A", "B"))
    assertThat(mapped.value).containsExactly("A", "B")
  }

  @Test
  fun `to list should propagate changes multiple times`() {
    var assertNextChange: (List<String?>?) -> Unit = {
      fail("should not be called")
    }

    val src = FXCollections.observableArrayList("A", "B", "C")
    val mapped = src.mapTo { it }

    System.gc()

    mapped.addListener { observable, oldValue, newValue ->
      assertNextChange(newValue)
    }

    assertThat(mapped.value).containsExactly("A", "B", "C")

    assertNextChange = {
      assertThat(it).containsExactly("A","B")
    }

    src.setAll(listOf("A", "B"))

    assertThat(mapped.value).containsExactly("A", "B")
  }


}