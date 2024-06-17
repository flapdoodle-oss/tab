package de.flapdoodle.tab.model.modifier

import de.flapdoodle.kfx.types.Id
import de.flapdoodle.tab.model.Name
import de.flapdoodle.tab.model.Node
import de.flapdoodle.tab.model.data.SingleValueId
import javafx.scene.paint.Color

data class ValueProperties(
    val id: Id<out Node.Constants>,
    val valueId: SingleValueId,
    val name: Name,
    val color: Color
) : ConstantModifier(
    id = id,
    change = { constants ->
        constants.copy(values = constants.values.change(valueId) { it.copy(name = name, color = color) })
    }
)
