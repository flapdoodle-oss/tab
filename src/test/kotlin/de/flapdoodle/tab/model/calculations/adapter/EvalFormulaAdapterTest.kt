package de.flapdoodle.tab.model.calculations.adapter

import de.flapdoodle.eval.core.evaluables.Evaluated
import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.calculations.Variable
import de.flapdoodle.tab.model.calculations.Variables
import de.flapdoodle.tab.model.calculations.interpolation.DefaultInterpolatorFactoryLookup
import de.flapdoodle.tab.model.calculations.types.IndexMap
import de.flapdoodle.tab.model.data.Column
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.InstanceOfAssertFactories
import org.assertj.core.data.Percentage
import org.junit.jupiter.api.Test
import java.math.BigDecimal
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
        val testee = EvalFormulaAdapter("index.month")
        val now = LocalDate.of(2023, Month.FEBRUARY, 3)

        assertThat(testee.variables())
            .hasSize(1)
        val (index) = testee.variables().toList()
        assertThat(index.name).isEqualTo("index")

        val result = testee.evaluate(mapOf(
            Variable(Variables.INDEX_NAME) to Evaluated.value(now),
        ))

        assertThat(result.wrapped()).isEqualTo(Month.FEBRUARY)
    }

    @Test
    fun arrayAccessWithIndexExample() {
        val testee = EvalFormulaAdapter("#b[index.month]")
        val now = LocalDate.of(2023, Month.FEBRUARY, 3)

        assertThat(testee.variables())
            .hasSize(2)
        val (b, index) = testee.variables().toList()
        assertThat(b.name).isEqualTo("#b")
        assertThat(b.isColumnReference).isTrue()
        assertThat(index.name).isEqualTo("index")

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
            Variable(Variables.INDEX_NAME) to Evaluated.value(now),
            Variable("#b") to columnWithInterpolator
        ))

        assertThat(result.wrapped()).isEqualTo(5.0)
    }

    @Test
    fun conditionalExample() {
        val testee = EvalFormulaAdapter("if (a<b,a/b,1)")

        assertThat(testee.variables())
            .hasSize(2)
        val (a,b) = testee.variables().toList()
        assertThat(a.name).isEqualTo("a")
        assertThat(b.name).isEqualTo("b")

        val result = testee.evaluate(mapOf(
            a to Evaluated.value(90),
            b to Evaluated.value(100.0),
        ))

        assertThat(result.wrapped())
            .isInstanceOf(BigDecimal::class.java)
            .asInstanceOf(InstanceOfAssertFactories.BIG_DECIMAL)
            .isCloseTo(BigDecimal.valueOf(0.9), Percentage.withPercentage(0.1))

        // TODO conditional gives some trouble
        val type = testee.evaluateType(mapOf(
            a to Evaluated.value(90),
            b to Evaluated.value(100.0),
        ))

        assertThat(type)
            .isEqualTo("")
    }
}