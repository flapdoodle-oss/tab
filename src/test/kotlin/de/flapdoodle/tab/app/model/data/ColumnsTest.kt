package de.flapdoodle.tab.app.model.data

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ColumnsTest {
    @Test
    fun addColumnIndexAndValue() {
        val column = Column("a", String::class, Int::class)

        val testee = Columns<String>()
            .addColumn(column)
            .add(column.id, "A", Int::class, 2)

        assertThat(testee.columns)
            .hasSize(1)

        val changedColumn = testee.columns[0]

        assertThat(changedColumn.id).isEqualTo(column.id)
        assertThat(changedColumn.values)
            .containsEntry("A",2)
            .hasSize(1)

        assertThat(testee.get(column.id, "A"))
            .isEqualTo(2)
        assertThat(testee.get(column.id, "B"))
            .isNull()
    }

    @Test
    fun sortedIndex() {
        val a = Column("a", Int::class, Int::class)
        val b = Column("b", Int::class, String::class)

        val testee = Columns<Int>()
            .addColumn(a)
            .addColumn(b)
            .add(a.id, 2, Int::class, 2)
            .add(b.id, 1, String::class, "Foo")

        assertThat(testee.index())
            .containsExactly(1,2)
    }
}