package de.flapdoodle.tab.bindings

import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PreboundBindingTest {

  @Test
  fun `understand invalidations - bind to one`() {
    val invalidationCalledFor = mutableListOf<Observable>()
    val changeLog = mutableListOf<String?>()

    val src = SimpleObjectProperty("fun")
    val testee = object : PreboundBinding<String>(src) {
      override fun computeValue(): String {
        return src.get()
      }
    }

    System.gc()

    testee.addListener(InvalidationListener {
      invalidationCalledFor.add(it)
    })
    testee.addListener(ChangeListener { observable, oldValue, newValue ->
      changeLog.add(newValue)
    })

    assertThat(testee.get()).isEqualTo("fun")
    assertThat(invalidationCalledFor).isEmpty()
    assertThat(changeLog).isEmpty()

    src.set("new")

    assertThat(testee.get()).isEqualTo("new")
    assertThat(invalidationCalledFor).containsExactly(testee)
    assertThat(changeLog).containsExactly("new")
  }


  @Test
  fun `understand invalidations - bind to two`() {
    val invalidationCalledFor = mutableListOf<Observable>()
    val changeLog = mutableListOf<String?>()

    val src1 = SimpleObjectProperty("foo")
    val src2 = SimpleObjectProperty("bar")

    val testee = object : PreboundBinding<String>(src1, src2) {
      override fun computeValue(): String {
        return src1.get()+ src2.get()
      }
    }

    System.gc()

    testee.addListener(InvalidationListener {
      invalidationCalledFor.add(it)
    })
    testee.addListener(ChangeListener { observable, oldValue, newValue ->
      changeLog.add(newValue)
    })

    assertThat(testee.get()).isEqualTo("foobar")
    assertThat(invalidationCalledFor).isEmpty()
    assertThat(changeLog).isEmpty()

    src1.set("FOO")

    assertThat(testee.get()).isEqualTo("FOObar")
    assertThat(invalidationCalledFor).containsExactly(testee)
    assertThat(changeLog).containsExactly("FOObar")

    src2.set("BAR")

    assertThat(testee.get()).isEqualTo("FOOBAR")
    assertThat(invalidationCalledFor).containsExactly(testee, testee)
    assertThat(changeLog).containsExactly("FOObar","FOOBAR")
  }
}