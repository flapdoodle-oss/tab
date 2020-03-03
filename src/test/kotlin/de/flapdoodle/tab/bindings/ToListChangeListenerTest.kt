package de.flapdoodle.tab.bindings

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.WeakChangeListener
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ToListChangeListenerTest {

  @Test
  fun `mapping must trigger change listener`() {
    var assertNextChange: (ListChangeListener.Change<out String>) -> Unit = {
      fail("should not be called")
    }

    val result = FXCollections.observableArrayList<String>()
    val src = SimpleObjectProperty(emptyList<String>())
    val testee = ToListChangeListener<List<String>, String>(result) { it }

    src.addListener(testee.wrapByWeakChangeListener())
    result.addListener(ListChangeListener {
      assertNextChange(it)
    })

    assertThat(result).isEmpty()

    assertNextChange = {
      assertThat(it.next()).describedAs("next").isTrue()
      assertThat(it.wasAdded()).describedAs("was added").isTrue()
      assertThat(it.from).describedAs("from").isEqualTo(0)
      assertThat(it.to).describedAs("to").isEqualTo(3)
      assertThat(it.next()).describedAs("next").isFalse()
    }
    src.set(listOf("A","B","C"))

    assertThat(result).containsExactly("A","B","C")

    assertNextChange = {
      assertThat(it.next()).describedAs("next").isTrue()
      assertThat(it.wasRemoved()).describedAs("was removed").isTrue()
      assertThat(it.from).describedAs("from").isEqualTo(2)
      assertThat(it.to).describedAs("to").isEqualTo(2)
      assertThat(it.next()).describedAs("next").isFalse()

      assertNextChange = {
        assertThat(it.next()).describedAs("next").isTrue()
        assertThat(it.wasReplaced()).describedAs("was replaced").isTrue()
        assertThat(it.wasUpdated()).describedAs("was updated").isFalse()
        assertThat(it.from).describedAs("from").isEqualTo(1)
        assertThat(it.to).describedAs("to").isEqualTo(2)
        assertThat(it.next()).describedAs("next").isFalse()

        assertNextChange = {
          fail("next")
        }
      }
    }
    src.set(listOf("A","C"))

    assertThat(result).containsExactly("A","C")
  }
}