package de.flapdoodle.tab.persist

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.flapdoodle.tab.data.ColumnId
import de.flapdoodle.tab.data.NamedColumn
import de.flapdoodle.tab.data.TabModel
import de.flapdoodle.tab.data.calculations.EvalExCalculationAdapter
import de.flapdoodle.tab.data.nodes.ColumnConnection
import de.flapdoodle.tab.data.nodes.ConnectableNode
import de.flapdoodle.tab.data.values.Variable
import de.flapdoodle.tab.persist.adapter.BigDecimalAdapter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class PersistableTabModelTest {

  @Test
  fun persist() {
    val moshi = Moshi.Builder()
//        .add(JsonTabModel.Adapter)
        .add(BigDecimalAdapter)
        .add(KotlinJsonAdapterFactory())
        .build()
    val modelAdapter = moshi.adapter(PersistableTabModel::class.java)
        .indent("  ")

    val numberColumnId = ColumnId.create(BigDecimal::class)
    val otherColumnId = ColumnId.create<BigDecimal>()

    val testTable = ConnectableNode.Table("test")
        .add(numberColumnId, "number")
    val calcTable = ConnectableNode.Calculated("calc")
        .add(NamedColumn("other", otherColumnId), EvalExCalculationAdapter("a+b"))

    val model = TabModel()
        .add(testTable)
        .add(calcTable)
        .connect(calcTable.id, Variable(BigDecimal::class,"a"), ColumnConnection.ColumnValues(numberColumnId))
        .applyDataChanges { data -> data.change(numberColumnId,2, BigDecimal.valueOf(123.456789)) }


    val persistableTabModel = PersistableTabModel.toPersistable(model)

    val json = modelAdapter.toJson(persistableTabModel)
    println("--------------------")
    println(json)
    println("--------------------")

    val readBack = modelAdapter.fromJson(json)
    assertThat(persistableTabModel).isEqualTo(readBack)

//    assertThat(json).isEqualTo("{\"name\":\"test\"}")
  }
}