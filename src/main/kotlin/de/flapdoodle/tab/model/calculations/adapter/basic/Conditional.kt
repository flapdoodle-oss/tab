package de.flapdoodle.tab.model.calculations.adapter.basic

import de.flapdoodle.eval.core.evaluables.TypedEvaluable
import de.flapdoodle.eval.core.evaluables.TypedEvaluable.of
import de.flapdoodle.tab.model.calculations.adapter.Evaluables

object Conditional : Evaluables(
    of(javaAny, booleanParameter, javaAnyParameterLazy, javaAnyParameterLazy, TypedEvaluable.Arg3 { _,_,_,check,l,r ->
        if (check) l else r
    })
)