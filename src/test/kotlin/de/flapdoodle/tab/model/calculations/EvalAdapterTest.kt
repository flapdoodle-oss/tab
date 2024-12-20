package de.flapdoodle.tab.model.calculations

import de.flapdoodle.tab.model.calculations.adapter.Eval
import de.flapdoodle.tab.model.calculations.adapter.EvalFormulaAdapter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EvalAdapterTest {

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
    fun conditions() {
        val testee = EvalFormulaAdapter("if(a<2,b,1)")

        assertThat(testee.variables())
            .hasSize(2)
        val (a, b) = testee.variables().toList()
        assertThat(a.name).isEqualTo("a")
        assertThat(b.name).isEqualTo("b")
    }
}