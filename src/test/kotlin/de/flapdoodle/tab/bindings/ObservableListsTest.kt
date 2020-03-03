package de.flapdoodle.tab.bindings

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tornadofx.*
import java.lang.ref.WeakReference
import java.util.Collections

internal class ObservableListsTest {

  @Test
  fun `sync should work`() {
    val src = FXCollections.observableArrayList<String>()
    val dst = FXCollections.observableList(mutableListOf("stuff"))

    val registration = ObservableLists.addMappedSync(src, dst) { ">$it<" }

    System.gc()

    assertThat(dst).isEmpty()

    src.addAll("1", "two", "3", "four", "5!", "six")
    assertThat(dst)
        .containsExactly(">1<", ">two<", ">3<", ">four<", ">5!<", ">six<")

    src.remove("3")
    assertThat(dst)
        .containsExactly(">1<", ">two<", ">four<", ">5!<", ">six<")

    Collections.swap(src,0, 1)
    assertThat(dst)
        .containsExactly(">two<", ">1<", ">four<", ">5!<", ">six<")

    src.sortBy { it }
    assertThat(src)
        .containsExactly("1", "5!", "four", "six", "two")

    assertThat(dst)
        .containsExactly(">1<", ">5!<", ">four<", ">six<", ">two<")

    registration.remove()
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
    val dst = src.mapToList { it }

    assertThat(dst)
        .containsExactly("start")

    src.value = listOf("one", "two", "tree")

    assertThat(dst)
        .containsExactly("one", "two", "tree")

    src.value = listOf("one", "tree")

    assertThat(dst)
        .containsExactly("one", "tree")

    src.value = listOf()

    assertThat(dst)
        .isEmpty()

    src.value = listOf()

    assertThat(dst)
        .isEmpty()
  }

  @Test
  fun weak() {
    val weakReference = WeakReference(Registration {})

    assertThat(weakReference.get()).isNotNull

    System.gc()

    assertThat(weakReference.get()).isNull()
  }

  @Test
  fun weak2() {
    val src = FXCollections.observableArrayList<String>()
    val listener = WeakListChangeListener(DummyListChangeListener<String>())
    src.addListener(listener)

    assertThat(listener.wasGarbageCollected()).isFalse()

    System.gc()

    assertThat(listener.wasGarbageCollected()).isTrue()
  }

  @Test
  fun weak3() {
    val (src,listener) = listAndListener()

    assertThat(listener.wasGarbageCollected()).isFalse()

    System.gc()

    assertThat(listener.wasGarbageCollected()).isTrue()
  }

  @Test
  fun weak4() {
    val (src_dst,listener) = srcDstAndListener()

    assertThat(listener.wasGarbageCollected()).isFalse()

    System.gc()

    assertThat(listener.wasGarbageCollected()).isFalse()
  }

  companion object {
    class DummyListChangeListener<T> : ListChangeListener<T> {
      override fun onChanged(c: ListChangeListener.Change<out T>) {
      }
    }

    @JvmStatic
    private fun listAndListener(): Pair<ObservableList<String>, WeakListChangeListener<String>> {
      val src = FXCollections.observableArrayList<String>()
      val dst = FXCollections.observableArrayList<String>()

      val delegate = DummyListChangeListener<String>()
      val listener = WeakListChangeListener(delegate)

      src.addListener(listener)
      dst.addListener(ListChangeListeners.keepReferenceTo(delegate))

      return Pair(src,listener)
    }

    @JvmStatic
    private fun srcDstAndListener(): Pair<Pair<ObservableList<String>, ObservableList<String>>, WeakListChangeListener<String>> {
      val src = FXCollections.observableArrayList<String>()
      val dst = FXCollections.observableArrayList<String>()

      val delegate = DummyListChangeListener<String>()
      val listener = WeakListChangeListener(delegate)

      src.addListener(listener)
      dst.addListener(ListChangeListeners.keepReferenceTo(delegate))

      return Pair(Pair(src,dst),listener)
    }
  }
}