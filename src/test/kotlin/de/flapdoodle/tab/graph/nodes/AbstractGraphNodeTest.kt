package de.flapdoodle.tab.graph.nodes

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class AbstractGraphNodeTest {

  @Test
  fun `is this a kotlin bug`() {
    val testee = Derived()

    // this should be wrong..
    assertEquals(listOf(null,"Bar"), testee.all)
    assertEquals(listOf("Foo","Bar"), testee.other)
  }

  abstract class BaseClass {
    abstract val foo: String
    abstract fun bar(): String

    val all = listOf(foo,bar())
    val other: List<String>
      get() = listOf(foo,bar())
  }

  class Derived : BaseClass() {
    override val foo = "Foo"
    override fun bar() = "Bar"
  }
}