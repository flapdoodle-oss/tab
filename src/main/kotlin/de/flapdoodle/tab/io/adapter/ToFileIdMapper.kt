package de.flapdoodle.tab.io.adapter

import de.flapdoodle.kfx.types.Id

fun interface ToFileIdMapper {
    fun idFor(id: Id<out Any>): String

    companion object {
        fun justCounting() = JustCounting()
        
        class JustCounting : ToFileIdMapper {
            private var counter: Int = 1
            private var map = emptyMap<Id<out Any>, Int>()

            override fun idFor(id: Id<out Any>): String {
                var mapped = map[id]
                if (mapped==null) {
                    mapped = counter++
                    map = map + (id to mapped)
                }
                return mapped.toString()
            }

        }
    }
}