package de.flapdoodle.tab.model.calculations.adapter

import de.flapdoodle.eval.core.evaluables.Evaluated
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.calculations.Variable
import de.flapdoodle.tab.model.calculations.interpolation.DefaultInterpolatorFactoryLookup
import de.flapdoodle.tab.model.calculations.types.IndexMap
import de.flapdoodle.tab.model.data.Column
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month

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
        val testee = EvalFormulaAdapter("a.index.month")
        val now = LocalDate.of(2023, Month.FEBRUARY, 3)

        assertThat(testee.variables())
            .hasSize(1)
        val (a) = testee.variables().toList()
        assertThat(a.name).isEqualTo("a")

        val result = testee.evaluate(mapOf(
            Variable("__index__") to Evaluated.value(now),
            Variable("a") to Evaluated.value("blub"),
        ))

        assertThat(result.wrapped()).isEqualTo(Month.FEBRUARY)
    }

    @Test
    fun arrayAccesWithIndexPropertyExample() {
        val testee = EvalFormulaAdapter("#b[a.index.month]")
        val now = LocalDate.of(2023, Month.FEBRUARY, 3)

        assertThat(testee.variables())
            .hasSize(2)
        val (b, a) = testee.variables().toList()
        assertThat(b.name).isEqualTo("#b")
        assertThat(b.isColumnReference).isTrue()
        assertThat(a.name).isEqualTo("a")

        val columnWithInterpolator: Evaluated<*> = Evaluated.value(
            IndexMap.asMap(
                column = Column(
                    name = Name("b"),
                    indexType = TypeInfo.of(Month::class.javaObjectType),
                    valueType = TypeInfo.of(Double::class.javaObjectType),
                    values = mapOf(Month.JANUARY to 0.0, /*Month.FEBRUARY to 5.0,*/ Month.MARCH to 10.0),
                ),
                interpolatorFactoryLookup = DefaultInterpolatorFactoryLookup
            )
        )

        val result = testee.evaluate(mapOf(
            Variable("__index__") to Evaluated.value(now),
            Variable("a") to Evaluated.value("blub"),
            Variable("#b") to columnWithInterpolator
        ))

        assertThat(result.wrapped()).isEqualTo(5.0)
    }
}