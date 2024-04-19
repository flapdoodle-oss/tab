package de.flapdoodle.tab.app.io.mapper

import de.flapdoodle.tab.app.io.adapter.ToFileMapping
import de.flapdoodle.tab.app.io.adapter.ToModelMapping
import de.flapdoodle.tab.app.io.file.FileFormula
import de.flapdoodle.tab.app.model.calculations.Formula
import de.flapdoodle.tab.app.model.calculations.adapter.EvalFormulaAdapter

class DefaultFormulaMapper(
    val evalFormulaAdapterMapper: Mapper<EvalFormulaAdapter, FileFormula> = EvalFormulaAdapterMapper()
) : Mapper<Formula, FileFormula> {
    override fun toFile(toFileMapping: ToFileMapping, src: Formula): FileFormula {
        require(src is EvalFormulaAdapter) { "not implemented: $src" }
        return evalFormulaAdapterMapper.toFile(toFileMapping, src)
    }

    override fun toModel(toModelMapping: ToModelMapping, src: FileFormula): Formula {
        return evalFormulaAdapterMapper.toModel(toModelMapping, src)
    }
}