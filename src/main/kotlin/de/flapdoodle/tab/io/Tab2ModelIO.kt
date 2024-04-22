package de.flapdoodle.tab.io

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.flapdoodle.tab.io.adapter.BigDecimalAdapter
import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.Tab2File
import de.flapdoodle.tab.io.mapper.DefaultModelFileMapper
import de.flapdoodle.tab.model.Tab2Model

object Tab2ModelIO {
    private val moshi = Moshi.Builder()
//        .add(JsonTabModel.Adapter)
        .add(BigDecimalAdapter)
        .add(KotlinJsonAdapterFactory())
        .build()

    private val modelAdapter = de.flapdoodle.tab.io.Tab2ModelIO.moshi.adapter(Tab2File::class.java)
        .indent("  ")
        

    fun asJson(model: Tab2Model): String {
        val file = DefaultModelFileMapper().toFile(ToFileMapping(), model)
//        val persistable = PersistableTabModel.toPersistable(model)
//        val file = TabFile(
//            model = persistable,
//            nodePositions = PersistableNodePositions.toPersistable(nodePositions)
//        )
//        return modelAdapter.toJson(file)
        return de.flapdoodle.tab.io.Tab2ModelIO.modelAdapter.toJson(file)
    }

    fun fromJson(json: String): Tab2Model {
        val file = de.flapdoodle.tab.io.Tab2ModelIO.modelAdapter.fromJson(json)
        require(file!=null) {"could not parse $json"}
        return DefaultModelFileMapper().toModel(ToModelMapping(), file)
//        val context = FromPersistableContext()
//
//        val model = PersistableTabModel.from(context, file.model)
//        val nodePositions = PersistableNodePositions.from(context, file.nodePositions)
//
//        return Pair(model,nodePositions)
    }

}