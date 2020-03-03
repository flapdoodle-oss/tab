package de.flapdoodle.tab.bindings

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import tornadofx.*
import java.util.Collections
import kotlin.collections.sort

internal class MappingListChangeListenerTest {
  @Test
  fun `mapping must trigger change listener`() {
    var assertNextChange: (ListChangeListener.Change<out String>) -> Unit = {
      fail("should not be called")
    }

    val result = FXCollections.observableArrayList<String>()
    val src = FXCollections.observableArrayList<String>()
    val testee = MappingListChangeListener<String, String>(result) { it }

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

      assertNextChange = {
        fail("next")
      }
    }
    src.addAll(listOf("A", "B", "C"))

    assertThat(result).containsExactly("A", "B", "C")

    assertNextChange = {
      assertThat(it.next()).describedAs("next").isTrue()
      assertThat(it.wasRemoved()).describedAs("was removed").isTrue()
      assertThat(it.from).describedAs("from").isEqualTo(1)
      assertThat(it.to).describedAs("to").isEqualTo(1)
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
    src.remove(1, 2)

    assertThat(result).containsExactly("A", "C")

    assertNextChange = {
      print(it)
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

    src.set(1, "D")

    assertThat(result).containsExactly("A", "D")

    assertNextChange = {
      println("--> $it")
      assertThat(it.next()).describedAs("next").isTrue()
      assertThat(it.wasReplaced()).describedAs("was replaced").isTrue()
      assertThat(it.from).describedAs("from").isEqualTo(1)
      assertThat(it.to).describedAs("to").isEqualTo(2)
      assertThat(it.next()).describedAs("next").isFalse()

      assertNextChange = {
        assertThat(it.next()).describedAs("next").isTrue()
        assertThat(it.wasReplaced()).describedAs("was replaced").isTrue()
        assertThat(it.from).describedAs("from").isEqualTo(0)
        assertThat(it.to).describedAs("to").isEqualTo(1)
        assertThat(it.next()).describedAs("next").isFalse()

        assertNextChange = {
          fail("next")
        }
      }
    }

    Collections.sort(src, Comparator { a, b -> b.compareTo(a) })

    assertThat(result).containsExactly("D", "A")

    assertNextChange = {
      println(it)
      assertThat(it.next()).describedAs("next").isTrue()
      assertThat(it.wasReplaced()).describedAs("was replaced").isTrue()
      assertThat(it.from).describedAs("from").isEqualTo(0)
      assertThat(it.to).describedAs("to").isEqualTo(3)
      assertThat(it.next()).describedAs("next").isFalse()

      assertNextChange = {
        fail("fail")
      }
    }

    src.setAll(listOf("1","2","3"))

    assertThat(result).containsExactly("1", "2", "3")
  }
}