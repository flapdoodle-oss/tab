package de.flapdoodle.tab.io.mapper

import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.FileFormula
import de.flapdoodle.tab.model.calculations.Formula
import de.flapdoodle.tab.model.calculations.adapter.EvalFormulaAdapter

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