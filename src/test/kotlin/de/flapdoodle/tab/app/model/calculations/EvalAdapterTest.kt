package de.flapdoodle.tab.app.model.calculations

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EvalAdapterTest {

    @Test
    fun simpleSample() {
        val testee = EvalAdapter("a*2+X-c")
        
        assertThat(testee.variables())
            .hasSize(3)
        val (a, X, c) = testee.variables().toList()
        assertThat(a.name).isEqualTo("a")
        assertThat(X.name).isEqualTo("X")
        assertThat(c.name).isEqualTo("c")

        val changeName = testee.changeFormula("b*2+X-c")

        assertThat(changeName.variables())
            .containsExactly(a.copy(name = "b"), X, c)

        val changePositionAndFormula = changeName.changeFormula("2*b+X*d")

        assertThat(changePositionAndFormula.variables())
            .hasSize(3)

        val (b, stillX, d) = changePositionAndFormula.variables().toList()
        assertThat(b).isEqualTo(a.copy(name = "b"))
        assertThat(stillX).isEqualTo(X)
        assertThat(d.name).isEqualTo("d")
        assertThat(d.id).isNotEqualTo(c.id)
    }
}