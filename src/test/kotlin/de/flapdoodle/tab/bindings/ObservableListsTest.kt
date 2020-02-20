package de.flapdoodle.tab.bindings

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tornadofx.*

internal class ObservableListsTest {

  @Test
  fun `sync should work`() {
    val src = FXCollections.observableArrayList<String>()
    val dst = FXCollections.observableList(mutableListOf("stuff"))

    ObservableLists.addMappedSync(src, dst) { ">$it<" }

    assertThat(dst).isEmpty()

    src.addAll("1", "two", "3", "four", "5!", "six")
    assertThat(dst)
        .containsExactly(">1<", ">two<", ">3<", ">four<", ">5!<", ">six<")

    src.remove("3")
    assertThat(dst)
        .containsExactly(">1<", ">two<", ">four<", ">5!<", ">six<")

    src.swap(0, 1)
    assertThat(dst)
        .containsExactly(">two<", ">1<", ">four<", ">5!<", ">six<")

    src.sortBy { it }
    assertThat(src)
        .containsExactly("1", "5!", "four", "six", "two")

    assertThat(dst)
        .containsExactly(">1<", ">5!<", ">four<", ">six<", ">two<")
  }

  @Test
  fun `permutation with 3 entries`() {
    val src = FXCollections.observableArrayList<String>()
    val dst = FXCollections.observableList(mutableListOf<String>())

    ObservableLists.addMappedSync(src, dst) { ">$it<" }

    src.addAll("1", "2", "0")
    src.sortBy { it }
    assertThat(src)
        .containsExactly("0", "1", "2")

    assertThat(dst)
        .containsExactly(">0<", ">1<", ">2<")
  }

  @Test
  fun `permutation with 4 entries`() {
    val src = FXCollections.observableArrayList<String>()
    val dst = FXCollections.observableList(mutableListOf<String>())

    ObservableLists.addMappedSync(src, dst) { ">$it<" }

    src.addAll("1", "7", "2", "0")
    src.sortBy { it }
    assertThat(src)
        .containsExactly("0", "1", "2", "7")

    assertThat(dst)
        .containsExactly(">0<", ">1<", ">2<", ">7<")
  }

  @Test
  fun `permutation with 4 entries and first unmodified`() {
    val src = FXCollections.observableArrayList<String>()
    val dst = FXCollections.observableList(mutableListOf<String>())

    ObservableLists.addMappedSync(src, dst) { ">$it<" }

    src.addAll("0", "7", "2", "1")
    src.sortBy { it }
    assertThat(src)
        .containsExactly("0", "1", "2", "7")

    assertThat(dst)
        .containsExactly(">0<", ">1<", ">2<", ">7<")
  }

  @Test
  fun `map observable to list`() {
    val src = SimpleObjectProperty<List<String>>(listOf("start"))
    val dst = ObservableLists.mapToList(src) { it }

    assertThat(dst)
        .containsExactly("start")

    src.value = listOf("one","two","tree")

    assertThat(dst)
        .containsExactly("one","two","tree")

    src.value = listOf("one","tree")

    assertThat(dst)
        .containsExactly("one","tree")

    src.value = listOf()

    assertThat(dst)
        .isEmpty()

    src.value = listOf()

    assertThat(dst)
        .isEmpty()
  }
}