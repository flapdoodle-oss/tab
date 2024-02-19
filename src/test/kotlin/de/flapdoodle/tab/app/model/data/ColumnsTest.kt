package de.flapdoodle.tab.app.model.data

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ColumnsTest {
    @Test
    fun sample() {
        val column = Column("a", String::class, Int::class)

        val testee = Columns<String>()
            .addColumn(column)
            .addIndex("A")
            .add(column.id, "A", 2)

        assertThat(testee.columns)
            .hasSize(1)

        val changedColumn = testee.columns[0]
        assertThat(changedColumn.id).isEqualTo(column.id)
        assertThat(changedColumn.values)
            .containsEntry("A",2)
            .hasSize(1)
    }
}