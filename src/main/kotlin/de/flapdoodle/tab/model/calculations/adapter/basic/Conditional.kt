package de.flapdoodle.tab.model.calculations.adapter.basic

import de.flapdoodle.eval.core.evaluables.TypedEvaluable
import de.flapdoodle.eval.core.evaluables.TypedEvaluable.of
import de.flapdoodle.tab.model.calculations.adapter.Evaluables

object Conditional : Evaluables(
    // TODO hmmm..
    of(javaAny, javaBooleanParameter, javaAnyParameterLazy, javaAnyParameterLazy, TypedEvaluable.Arg3 { _,_,_,check,l,r ->
//        println("check: $check")
//        println("left: $l (${l.javaClass})")
//        println("right: $r (${r.javaClass})")
        if (check) l else r
    })
)