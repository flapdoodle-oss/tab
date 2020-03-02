package de.flapdoodle.tab.bindings

import javafx.beans.property.SimpleStringProperty
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

internal class BindingsTest {

  @Nested
  inner class MapNullable {

    @Test
    fun `must propagate`() {
      var lastValueFromChangeListener: String? = null

      val src = SimpleStringProperty("start")
      val mapped = src.mapNullable { ">$it<" }

      System.gc()

      mapped.addListener { observable, oldValue, newValue ->
        lastValueFromChangeListener = newValue
      }

      assertThat(mapped.value).isEqualTo(">start<")
      assertThat(lastValueFromChangeListener).isNull()

      src.value = "new"
      assertThat(lastValueFromChangeListener).isEqualTo(">new<")
    }

    @Test
    fun `must propagate two levels`() {
      var lastValueFromChangeListener: String? = null

      val src = SimpleStringProperty("start")
      val mapped = src.mapNullable { ">$it<" }.mapNullable { "[$it]" }

      System.gc()

      mapped.addListener { observable, oldValue, newValue ->
        lastValueFromChangeListener = newValue
      }

      assertThat(mapped.value).isEqualTo("[>start<]")
      assertThat(lastValueFromChangeListener).isNull()

      src.value = "new"
      assertThat(lastValueFromChangeListener).isEqualTo("[>new<]")
    }

    @Test
    fun `mapNonNull must fail if source is null`() {
      var lastValueFromChangeListener: String? = null

      val src = SimpleStringProperty("start")
      val mapped = src.mapNonNull { ">$it<" }

      System.gc()

      mapped.addListener { observable, oldValue, newValue ->
        lastValueFromChangeListener = newValue
      }

      assertThat(mapped.value).isEqualTo(">start<")
      assertThat(lastValueFromChangeListener).isNull()

      src.value = "new"
      assertThat(lastValueFromChangeListener).isEqualTo(">new<")

      Thread.currentThread().uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { thread, throwable ->
        throw throwable
      }

      assertThatThrownBy {
        src.value = null
      }.isInstanceOf(IllegalArgumentException::class.java)

      assertThat(lastValueFromChangeListener).isEqualTo(">new<")
    }
  }

  @Nested
  inner class Combine {

    @Test
    fun `combine must trigger change for each source`() {
      var lastValueFromChangeListener: String? = null

      val src1 = SimpleStringProperty("foo")
      val src2 = SimpleStringProperty("bar")
      val mapped = src1.mergeWith(src2) { a,b -> "$a$b" }

      System.gc()

      mapped.addListener { observable, oldValue, newValue ->
        lastValueFromChangeListener = newValue
      }

      assertThat(mapped.value).isEqualTo("foobar")
      assertThat(lastValueFromChangeListener).isNull()

      src1.value = "FOO"
      assertThat(lastValueFromChangeListener).isEqualTo("FOObar")

      src2.value = "BAR"
      assertThat(lastValueFromChangeListener).isEqualTo("FOOBAR")
    }
  }
}