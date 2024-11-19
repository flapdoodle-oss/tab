package de.flapdoodle.tab.model.calculations.adapter.booleans

import de.flapdoodle.eval.core.evaluables.TypedEvaluable.of
import de.flapdoodle.tab.model.calculations.adapter.Evaluables

object Combine {
    object And : Evaluables(
        of(javaBoolean,javaBooleanParameter,javaBooleanParameter,Arg2Math<Boolean, Boolean, Boolean> { l, r, _ -> l && r })
    )
    object Or : Evaluables(
        of(javaBoolean,javaBooleanParameter,javaBooleanParameter,Arg2Math<Boolean, Boolean, Boolean> { l, r, _ -> l || r })
    )
    object Not : Evaluables(
        of(javaBoolean,javaBooleanParameter,ArgMath<Boolean, Boolean> { l, _ -> !l })
    )
}