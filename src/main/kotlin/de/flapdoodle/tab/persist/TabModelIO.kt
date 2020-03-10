package de.flapdoodle.tab.persist

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.flapdoodle.tab.data.TabModel
import de.flapdoodle.tab.persist.adapter.BigDecimalAdapter

object TabModelIO {

  private val moshi = Moshi.Builder()
//        .add(JsonTabModel.Adapter)
      .add(BigDecimalAdapter)
      .add(KotlinJsonAdapterFactory())
      .build()

  private val modelAdapter = moshi.adapter(PersistableTabModel::class.java)
      .indent("  ")

  fun asJson(model: TabModel): String {
    val persistable = PersistableTabModel.toPersistable(model)
    return modelAdapter.toJson(persistable)
  }
}