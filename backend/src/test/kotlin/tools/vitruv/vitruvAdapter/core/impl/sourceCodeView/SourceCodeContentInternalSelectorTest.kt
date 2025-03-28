package tools.vitruv.vitruvAdapter.core.impl.sourceCodeView

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.mdsd.jamopp.model.java.containers.CompilationUnit
import tools.vitruv.vitruvAdapter.core.api.PreMappedWindow
import tools.vitruv.vitruvAdapter.utils.EObjectContainer

class SourceCodeContentInternalSelectorTest {
    private val selector = SourceCodeContentSelector()

    /**
     * @author Amir, Nico
     */
    @Test
    fun testApplySelection() {
        val javaPackage = EObjectContainer().getContainer1AsRootObjects()
        val selectedObjects = selector.applySelection(javaPackage, setOf("Class1"))
        val javaCompilationUnit = javaPackage[0] as CompilationUnit
        val javaClass = javaCompilationUnit.classifiers[0]

        val expectedSelectedWindows =
            listOf(
                PreMappedWindow<String>("Class1", mutableListOf(javaClass)),
            )
        assertEquals(expectedSelectedWindows, selectedObjects)
    }
}
