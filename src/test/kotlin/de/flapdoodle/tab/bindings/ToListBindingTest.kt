package de.flapdoodle.tab.bindings

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ListChangeListener
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ToListBindingTest {

  @Test
  fun `to list should propagate changes multiple times`() {
    var assertNextChange: (ListChangeListener.Change<out String>) -> Unit = {
      fail("should not be called")
    }

    val src = SimpleObjectProperty(listOf("A","B","C"))
    val mapped = src.mapToList {
      it
    }

    mapped.addListener(ListChangeListener {
      assertNextChange(it)
    })

    assertThat(mapped).containsExactly("A","B","C")

    assertNextChange = {
      assertThat(it.next()).isTrue()
      assertThat(it.wasRemoved()).isTrue()
      assertThat(it.from).describedAs("from").isEqualTo(2)
      assertThat(it.to).describedAs("to").isEqualTo(2)
      assertThat(it.next()).isFalse()

      assertNextChange = {
        fail("next")
      }
    }

    src.set(listOf("A","B"))

    assertThat(mapped).containsExactly("A","B")
  }
}