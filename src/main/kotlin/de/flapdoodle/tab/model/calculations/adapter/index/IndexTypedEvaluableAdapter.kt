package de.flapdoodle.tab.model.calculations.adapter.index

import de.flapdoodle.eval.core.EvaluationContext
import de.flapdoodle.eval.core.VariableResolver
import de.flapdoodle.eval.core.evaluables.Evaluated
import de.flapdoodle.eval.core.evaluables.Parameter
import de.flapdoodle.eval.core.evaluables.Signature
import de.flapdoodle.eval.core.evaluables.TypedEvaluable
import de.flapdoodle.eval.core.exceptions.EvaluableException
import de.flapdoodle.eval.core.parser.Token
import de.flapdoodle.eval.core.validation.ParameterValidator
import de.flapdoodle.reflection.TypeInfo
import java.util.*

object IndexTypedEvaluableAdapter : TypedEvaluable<Any> {
    private val signature = Signature.of(
        TypeInfo.of(Any::class.java),
        Parameter.of(TypeInfo.of(Any::class.java)),
        Parameter.of(TypeInfo.of(String::class.java))
            .withValidators(ParameterValidator { it ->
                if (it == "index") Optional.empty()
                else Optional.of(EvaluableException.of("unknown property: $it"))
            }),
    )

    override fun signature(): Signature<Any> {
        return signature
    }

    override fun evaluate(
        variableResolver: VariableResolver,
        evaluationContext: EvaluationContext?,
        token: Token?,
        arguments: MutableList<out Evaluated<*>>
    ): Evaluated<Any> {
        require(arguments.size==2) {"invalid arguments: $arguments"}
        val second = arguments[1]
        require(second.type()==TypeInfo.of(String::class.java)) { "wrong type: $second"}

        require(variableResolver.has("index")) {"no variable 'index'"}
        return variableResolver.get("index") as Evaluated<Any>
    }
}