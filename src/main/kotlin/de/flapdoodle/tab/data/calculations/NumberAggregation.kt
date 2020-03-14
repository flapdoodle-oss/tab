package de.flapdoodle.tab.data.calculations

import de.flapdoodle.tab.data.values.Input
import java.math.BigDecimal

data class NumberAggregation(
  val type: Type
) : Aggregation<BigDecimal> {

  enum class Type {
    Sum,
    Avg
  }

  override fun variable() = Input.List(BigDecimal::class,"input")

  override fun aggregate(lookup: Aggregation.VariableLookup): BigDecimal? {
    val src = lookup[variable()] ?: emptyList()
    val nonNullList = src.mapNotNull { it }
    return if (nonNullList.isNotEmpty()) {
      when (type) {
        Type.Sum -> nonNullList.reduce(BigDecimal::add)
        Type.Avg -> nonNullList.reduce(BigDecimal::add).div(BigDecimal.valueOf(nonNullList.size.toLong()))
      }
    } else null
  }

//  private fun op(left: BigDecimal?, right: BigDecimal?, op: (BigDecimal, BigDecimal) -> BigDecimal): BigDecimal? {
//    return if (left!=null) {
//      if (right!=null) op(left,right) else left
//    } else {
//      if (right!=null) {
//        if (left!=null) op(right,left) else right
//      } else null
//    }
//  }
}