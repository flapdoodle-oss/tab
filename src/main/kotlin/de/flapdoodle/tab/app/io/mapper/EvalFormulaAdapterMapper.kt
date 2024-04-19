package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping
import de.flapdoodle.tab.app.io.file.FileFormula
import de.flapdoodle.tab.app.io.file.FileVariable
import de.flapdoodle.tab.app.model.calculations.Variable
import de.flapdoodle.tab.app.model.calculations.adapter.Eval
import de.flapdoodle.tab.app.model.calculations.adapter.EvalFormulaAdapter

class EvalFormulaAdapterMapper(): Mapper<EvalFormulaAdapter, FileFormula> {
    override fun toFile(toFileMapping: ToFileMapping, src: EvalFormulaAdapter): FileFormula {
        return FileFormula(
            expression = src.expression(),
            variables = src.variables().map {
                FileVariable(it.name, toFileMapping.idFor(it.id))
            }.toSet()
        )
    }

    override fun toModel(toModelMapping: ToModelMapping, src: FileFormula): EvalFormulaAdapter {
        val idOfName = src.variables.associate { it.name to it.id }

        val expression = Eval.parse(src.expression)
        val variablesWithHash = expression.usedVariablesWithHash()
            .map { Variable(it.key, toModelMapping.nextId(idOfName[it.key]!!, Variable::class)) to it.value }

        return EvalFormulaAdapter(
            formula = src.expression,
            expression = expression,
            variablesWithHash = variablesWithHash
        )
    }
}