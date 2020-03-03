package de.flapdoodle.tab.bindings

import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class FlatmapListBindingTest {
  @Test
  fun `map must keep refernce to source to avoid GC`() {
    val src = FXCollections.observableArrayList(1,2,3)
    val mapped = src.gcAbleCopy().flatMapObservable { listOf("$it") }

    System.gc()

    assertThat(mapped).containsExactly("1", "2", "3")
    src.remove(1,2)
    assertThat(mapped).containsExactly("1", "3")
  }

  @Test
  fun `map each source item`() {
    var assertNextChange: (ListChangeListener.Change<out String>) -> Unit = {
      fail("should not be called")
    }

    val src = FXCollections.observableArrayList(1,2,3)
    val mapped = src.flatMapObservable { listOf("$it") }

    System.gc()

    mapped.addListener(ListChangeListener {
      assertNextChange(it)
    })

    assertThat(mapped).containsExactly("1", "2", "3")

    assertNextChange = {
      assertThat(it.next()).isTrue()
      assertThat(it.wasRemoved()).isTrue()
      assertThat(it.from).describedAs("from").isEqualTo(2)
      assertThat(it.to).describedAs("to").isEqualTo(2)
      assertThat(it.next()).isFalse()

      assertNextChange = {
        assertThat(it.next()).isTrue()
        assertThat(it.wasReplaced()).isTrue()
        assertThat(it.from).describedAs("from").isEqualTo(1)
        assertThat(it.to).describedAs("to").isEqualTo(2)
        assertThat(it.next()).isFalse()

        assertNextChange = {
          fail("next")
        }
      }
    }

    src.remove(1,2)

    assertThat(mapped).containsExactly("1", "3")
  }

}