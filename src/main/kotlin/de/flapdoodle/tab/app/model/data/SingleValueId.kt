package de.flapdoodle.tab.app.model.data

import de.flapdoodle.kfx.types.Id

data class SingleValueId(
    val id: Id<SingleValueId> = Id.nextId(SingleValueId::class)
): DataId()