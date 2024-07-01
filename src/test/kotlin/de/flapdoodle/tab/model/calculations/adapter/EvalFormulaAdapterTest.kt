package de.flapdoodle.tab.model.calculations.adapter

import de.flapdoodle.eval.core.evaluables.Evaluated
import de.flapdoodle.tab.model.calculations.Variable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EvalFormulaAdapterTest {

    @Test
    fun simpleSample() {
        val testee = EvalFormulaAdapter("a*2+X-c")

        assertThat(testee.variables())
            .hasSize(3)
        val (a, X, c) = testee.variables().toList()
        assertThat(a.name).isEqualTo("a")
        assertThat(X.name).isEqualTo("X")
        assertThat(c.name).isEqualTo("c")

        val changeName = testee.change(Eval.parse("b*2+X-c"))

        assertThat(changeName.variables())
            .containsExactly(a.copy(name = "b"), X, c)

        val changePositionAndFormula = changeName.change(Eval.parse("2*b+X*d"))

        assertThat(changePositionAndFormula.variables())
            .hasSize(3)

        val (b, stillX, d) = changePositionAndFormula.variables().toList()
        assertThat(b).isEqualTo(a.copy(name = "b"))
        assertThat(stillX).isEqualTo(X)
        assertThat(d.name).isEqualTo("d")
        assertThat(d.id).isNotEqualTo(c.id)
    }

    @Test
    fun indexPropertyExample() {
        val testee = EvalFormulaAdapter("a.index")

        assertThat(testee.variables())
            .hasSize(1)
        val (a) = testee.variables().toList()
        assertThat(a.name).isEqualTo("a")

        val result = testee.evaluate(mapOf(
            Variable("index") to Evaluated.value(1),
            Variable("a") to Evaluated.value("blub"),
        ))

        assertThat(result.wrapped()).isEqualTo(1)
    }

    @Test
    fun arrayAccesWithIndexPropertyExample() {
        val testee = EvalFormulaAdapter("b[a.index]")

        assertThat(testee.variables())
            .hasSize(2)
        val (b, a) = testee.variables().toList()
        assertThat(b.name).isEqualTo("b")
        assertThat(a.name).isEqualTo("a")

        val result = testee.evaluate(mapOf(
            Variable("a") to Evaluated.value("blub"),
            Variable("b") to Evaluated.value("FooBar")
        ))
    }
}