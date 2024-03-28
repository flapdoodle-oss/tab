package de.flapdoodle.tab.app.model.calculations.adapter

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

        val changeName = testee.change("b*2+X-c")

        assertThat(changeName.variables())
            .containsExactly(a.copy(name = "b"), X, c)

        val changePositionAndFormula = changeName.change("2*b+X*d")

        assertThat(changePositionAndFormula.variables())
            .hasSize(3)

        val (b, stillX, d) = changePositionAndFormula.variables().toList()
        assertThat(b).isEqualTo(a.copy(name = "b"))
        assertThat(stillX).isEqualTo(X)
        assertThat(d.name).isEqualTo("d")
        assertThat(d.id).isNotEqualTo(c.id)
    }
}