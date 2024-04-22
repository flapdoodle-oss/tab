package de.flapdoodle.tab.io.file

class FileDataId(
    val valueId: String? = null,
    val columnId: String? = null
) {
    init {
        require(valueId != null || columnId!=null) {"valueId and columnId not set"}
        require(!(valueId != null && columnId!=null)) {"valueId and columnId set"}
    }
}