package tools.vitruv.vitruvAdapter.core.impl.classTableView

data class ClassTableEntry(
    val uuid: String,
    val name: String,
    val visibility: String,
    val isAbstract: Boolean,
    val isFinal: Boolean,
    val superClassName: String,
    val interfaces: List<String>,
    val attributeCount: Int,
    val methodCount: Int,
    val linesOfCode : Int,
)