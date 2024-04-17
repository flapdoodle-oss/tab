package de.flapdoodle.tab.app.io

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.flapdoodle.tab.app.io.adapter.*
import de.flapdoodle.tab.app.io.file.Tab2File
import de.flapdoodle.tab.app.io.mapper.DefaultModelFileMapper
import de.flapdoodle.tab.app.model.Tab2Model
import de.flapdoodle.tab.data.NodePositions

object Tab2ModelIO {
    private val moshi = Moshi.Builder()
//        .add(JsonTabModel.Adapter)
        .add(BigDecimalAdapter)
        .add(KotlinJsonAdapterFactory())
        .build()

    private val modelAdapter = moshi.adapter(Tab2File::class.java)
        .indent("  ")

    fun asJson(model: Tab2Model, nodePositions: NodePositions): String {
        val file = DefaultModelFileMapper().toFile(ToFileMapping(), model)
//        val persistable = PersistableTabModel.toPersistable(model)
//        val file = TabFile(
//            model = persistable,
//            nodePositions = PersistableNodePositions.toPersistable(nodePositions)
//        )
//        return modelAdapter.toJson(file)
        return modelAdapter.toJson(file)
    }

    fun fromJson(json: String): Tab2Model {
        val file = modelAdapter.fromJson(json)
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