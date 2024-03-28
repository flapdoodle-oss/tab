package de.flapdoodle.tab.app.model.calculations.adapter

import de.flapdoodle.eval.core.exceptions.EvaluationException
import de.flapdoodle.eval.core.tree.EvaluableExceptionMapper
import java.util.*

object ExceptionMapper : EvaluableExceptionMapper {
    override fun map(ex: EvaluationException): Any {
        return Wrapper(ex)
    }

    override fun match(value: Any): Optional<EvaluationException> {
        if (value is Wrapper) {
            return Optional.of(value.ex)
        }
        return Optional.empty()
    }

    private class Wrapper(internal val ex: EvaluationException)
}