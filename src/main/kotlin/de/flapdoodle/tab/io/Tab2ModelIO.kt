package de.flapdoodle.tab.io

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.flapdoodle.tab.io.adapter.BigDecimalAdapter
import de.flapdoodle.tab.io.adapter.ToFileMapping
import de.flapdoodle.tab.io.adapter.ToModelMapping
import de.flapdoodle.tab.io.file.Tab2File
import de.flapdoodle.tab.io.mapper.DefaultModelFileMapper
import de.flapdoodle.tab.model.Model
import de.flapdoodle.tab.model.Tab2Model

object Tab2ModelIO {
    private val moshi = Moshi.Builder()
//        .add(JsonTabModel.Adapter)
        .add(BigDecimalAdapter)
        .add(KotlinJsonAdapterFactory())
        .build()

    private val modelAdapter = moshi.adapter(Tab2File::class.java)
        .indent("  ")
        

    fun asJson(model: Model): String {
        val file = DefaultModelFileMapper().toFile(ToFileMapping(), model)
        return modelAdapter.toJson(file)
    }

    fun fromJson(json: String): Model {
        val file = modelAdapter.fromJson(json)
        require(file!=null) {"could not parse $json"}
        return DefaultModelFileMapper().toModel(ToModelMapping(), file)
    }

}