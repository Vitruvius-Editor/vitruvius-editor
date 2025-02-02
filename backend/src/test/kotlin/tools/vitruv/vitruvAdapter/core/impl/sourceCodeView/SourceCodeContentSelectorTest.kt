package tools.vitruv.vitruvAdapter.core.impl.sourceCodeView

import org.eclipse.emf.ecore.EObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import tools.mdsd.jamopp.model.java.containers.CompilationUnit
import tools.mdsd.jamopp.parser.jdt.singlefile.JaMoPPJDTSingleFileParser
import java.io.FileInputStream


class SourceCodeContentSelectorTest {
    private lateinit var eObjects: List<EObject>

    @BeforeEach
    fun initObjects() {
        try {
            val inputStream =
                FileInputStream("src/test/kotlin/tools/vitruv/vitruvAdapter/core/impl/sourceCodeView/TestClass.txt")
            val compilationUnit = JaMoPPJDTSingleFileParser().parse("TestClass.txt", inputStream) as CompilationUnit
            eObjects = listOf(compilationUnit)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @Test
    fun testApplySelection() {
        val sourceCodeSelector = SourceCodeContentSelector()
        val selectedObjects = sourceCodeSelector.applySelection(eObjects, setOf("TestClass"))
        val compilationUnit = eObjects[0] as CompilationUnit
        val expectedObjects = compilationUnit.classifiers[0]
        assertEquals(expectedObjects, selectedObjects[0])
    }

}