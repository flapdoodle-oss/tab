package de.flapdoodle.tab.model.calculations.adapter.basic

import de.flapdoodle.eval.core.EvaluationContext
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.evaluables.TypedEvaluable
import de.flapdoodle.eval.core.evaluables.TypedEvaluable.of
import de.flapdoodle.eval.core.parser.Token
import de.flapdoodle.tab.model.calculations.adapter.Evaluables
import java.util.Objects

object Comparables {
    sealed class Compare<T: Comparable<T>>(
        val check: (Int) -> Boolean
    ): TypedEvaluable.Arg2<T, T, Boolean> {

        override fun evaluate(
            variableResolver: VariableResolver,
            evaluationContext: EvaluationContext,
            token: Token,
            first: T,
            second: T
        ): Boolean {
            return check(first.compareTo(second))
        }

        class CompareGreater<T: Comparable<T>>(): Compare<T>({it > 0})
        class CompareGreaterOrEqual<T: Comparable<T>>(): Compare<T>({it >= 0})
        class CompareLess<T: Comparable<T>>(): Compare<T>({it < 0})
        class CompareLessOrEqual<T: Comparable<T>>(): Compare<T>({it <= 0})
    }

    object Equals : Evaluables(
        of(boolean, javaAnyParameterLazy, javaAnyParameterLazy, TypedEvaluable.Arg2 { _,_,check,l,r ->
            Objects.equals(l, r)
        }),
    )
    object Less : Evaluables(
        of(boolean, javaDouble, javaDouble, Compare.CompareLess())
    )
    object LessOrEquals : Evaluables(
        of(boolean, javaDouble, javaDouble, Compare.CompareLessOrEqual())
    )
    object Greater : Evaluables(
        of(boolean, javaDouble, javaDouble, Compare.CompareGreater())
    )
    object GreaterOrEquals : Evaluables(
        of(boolean, javaDouble, javaDouble, Compare.CompareGreaterOrEqual())
    )
}