package de.flapdoodle.tab.model.data

import de.flapdoodle.reflection.TypeInfo
import de.flapdoodle.tab.model.data.Column
import de.flapdoodle.tab.model.data.Columns
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ColumnsTest {
    @Test
    fun addColumnIndexAndValue() {
        val column = Column("a", TypeInfo.of(String::class.javaObjectType), TypeInfo.of(Int::class.javaObjectType))

        val testee = Columns<String>()
            .addColumn(column)
            .add(column.id, "A", TypeInfo.of(Int::class.javaObjectType), 2)

        assertThat(testee.columns())
            .hasSize(1)

        val changedColumn = testee.columns()[0]

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
        val a = Column("a", TypeInfo.of(Int::class.javaObjectType), TypeInfo.of(Int::class.javaObjectType))
        val b = Column("b", TypeInfo.of(Int::class.javaObjectType), TypeInfo.of(String::class.javaObjectType))

        val testee = Columns<Int>()
            .addColumn(a)
            .addColumn(b)
            .add(a.id, 2, TypeInfo.of(Int::class.javaObjectType), 2)
            .add(b.id, 1, TypeInfo.of(String::class.javaObjectType), "Foo")

        assertThat(testee.index())
            .containsExactly(1,2)
    }
}