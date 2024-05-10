package de.flapdoodle.tab.model.calculations.adapter

import de.flapdoodle.eval.core.evaluables.Evaluated
import de.flapdoodle.eval.core.exceptions.EvaluationException
import de.flapdoodle.eval.core.tree.EvaluableExceptionMapper
import java.util.*

object ExceptionMapper : EvaluableExceptionMapper {
    override fun map(ex: EvaluationException): Any {
        return Wrapper(ex)
    }

    override fun match(value: Evaluated<out Any>): Optional<EvaluationException> {
        val wrapped = value.wrapped()
        if (wrapped is Wrapper) {
            return Optional.of(wrapped.ex)
        }
        return Optional.empty()
    }

    private data class Wrapper(internal val ex: EvaluationException)
}