package de.flapdoodle.tab.persist

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.flapdoodle.tab.data.NodePositions
import de.flapdoodle.tab.data.TabModel
import de.flapdoodle.tab.persist.PersistableTabModel.Companion.from
import de.flapdoodle.tab.persist.adapter.BigDecimalAdapter

object TabModelIO {

  private val moshi = Moshi.Builder()
//        .add(JsonTabModel.Adapter)
      .add(BigDecimalAdapter)
      .add(KotlinJsonAdapterFactory())
      .build()

  private val modelAdapter = moshi.adapter(TabFile::class.java)
      .indent("  ")

  fun asJson(model: TabModel, nodePositions: NodePositions): String {
    val persistable = PersistableTabModel.toPersistable(model)
    val file = TabFile(
        model = persistable,
        nodePositions = PersistableNodePositions.toPersistable(nodePositions)
    )
    return modelAdapter.toJson(file)
  }

  fun fromJson(json: String): Pair<TabModel, NodePositions> {
    val file = modelAdapter.fromJson(json)
    require(file!=null) {"could not parse $json"}
    val context = FromPersistableContext()

    val model = from(context, file.model)
    val nodePositions = PersistableNodePositions.from(context, file.nodePositions)

    return Pair(model,nodePositions)
  }
}