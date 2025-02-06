package tools.vitruv.vitruvAdapter.core.impl.sourceCodeView

import org.eclipse.emf.ecore.EObject
import tools.mdsd.jamopp.model.java.containers.JavaRoot
import tools.mdsd.jamopp.printer.JaMoPPPrinter
import tools.vitruv.vitruvAdapter.core.api.DisplayContentMapper
import tools.vitruv.vitruvAdapter.core.api.Window
import tools.vitruv.vitruvAdapter.core.impl.displayContentMapper.TextDisplayContentMapper
import tools.vitruv.vitruvAdapter.core.impl.abstractMapper.TextViewMapper
import java.io.ByteArrayOutputStream
import org.eclipse.jdt.core.ToolFactory
import org.eclipse.jdt.core.formatter.CodeFormatter
import org.eclipse.jface.text.Document
import org.eclipse.text.edits.TextEdit
import org.eclipse.uml2.uml.Interface
import tools.mdsd.jamopp.model.java.classifiers.Class
import tools.mdsd.jamopp.model.java.members.MemberContainer
import tools.mdsd.jamopp.parser.jdt.JaMoPPJDTParser
import tools.mdsd.jamopp.parser.jdt.singlefile.JaMoPPJDTSingleFileParser
import tools.vitruv.vitruvAdapter.core.api.PreMappedWindow


/**
 * This class maps java classes to a string source code representation
 * Based on the java MetaModel
 */
class SourceCodeViewMapper : TextViewMapper() {

    /**
     * Maps the given view content to a list of windows.
     * @param preMappedWindows the pre-mapped windows to map to windows.
     * @return The windows representing the view content.
     */
    override fun mapEObjectsToWindowsContent(preMappedWindows: List<PreMappedWindow<String>>): List<Window<String>> {
        val windows = mutableListOf<Window<String>>()

        for (preMappedWindow in preMappedWindows) {
            for (classifier in preMappedWindow.neededEObjects) {
                if (classifier is tools.mdsd.jamopp.model.java.classifiers.ConcreteClassifier) {
                    val outputStream = ByteArrayOutputStream()
                    JaMoPPPrinter.print(classifier, outputStream)
                    var window: Window<String>
                    val code = outputStream.toString()
                    val formattedCode = formatJavaCode(code)
                    if(formattedCode != null){
                        windows.add(preMappedWindow.createWindow(formattedCode))
                    }else{
                        windows.add(preMappedWindow.createWindow(code))
                    }
                }
            }
        }

        return windows.toList()
    }

    fun formatJavaCode(code: String): String? {
        // Create a CodeFormatter with default options (you can also supply a map for custom settings)
        val formatter: CodeFormatter = ToolFactory.createCodeFormatter(null)
        // Format the code as a compilation unit (use CodeFormatter.K_COMPILATION_UNIT)
        val textEdit: TextEdit? = formatter.format(
            CodeFormatter.K_COMPILATION_UNIT,
            code,
            0,
            code.length,
            0,
            null
        )

        return (if (textEdit != null) {
            val document = Document(code)
            textEdit.apply(document)
            document.get()
        } else {
            null // formatting failed
        }).toString()
    }



    /**
     * Maps the given json string to view content, compares it to [preMappedWindows] and applies the changes to the eObjects of [preMappedWindows].
     * Note that no changes will be applied to the model,
     * this have to be done after this method with a View object, where the [preMappedWindows] came from.
     * @param preMappedWindows The pre-mapped windows to compare the windows to.
     * @param windows the windows to map to EObjects.
     * @return The view content.
     */
    override fun mapWindowsToEObjectsAndApplyChangesToEObjects(
        preMappedWindows: List<PreMappedWindow<String>>,
        windows: List<Window<String>>
    ): List<EObject> {
        val windowPairs = pairWindowsTogether(preMappedWindows, windows)
        for (item in windowPairs) {
            applyChangesToWindow(item.second, item.first)
        }
        return listOf() //unnecesary return value, has to be changed
    }

    private fun applyChangesToWindow(window: Window<String>, preMappedWindow: PreMappedWindow<String>) {
        val parser = JaMoPPJDTSingleFileParser()
        val inputStream = window.content.byteInputStream()
        val javaRoot = parser.parse(preMappedWindow.name, inputStream)

        checkIfOnlyMethodBodiesChanged(preMappedWindow.neededEObjects, javaRoot)
        applyChangesToMethodBodies(preMappedWindow.neededEObjects, javaRoot)
    }



    private fun checkIfOnlyMethodBodiesChanged(oldEObjects: List<EObject>, javaRoot: JavaRoot) {
        TODO()
    }

    private fun applyChangesToMethodBodies(oldEObjects: List<EObject>, javaRoot: JavaRoot) {
        for (oldEObject in oldEObjects) {
            val compilationUnit = javaRoot.containingCompilationUnit
            if (oldEObject is MemberContainer) {
                val javaClass = compilationUnit.containedClass
                for (method in oldEObject.methods) {
                    val javaMethod = javaClass.methods.find { it.name == method.name }
                    if (javaMethod != null) {
                        method.statements.clear()
                        method.statements.addAll(javaMethod.statements)
                    }
                }
            }
        }
    }


    override fun mapViewToWindows(rootObjects: List<EObject>): Set<String> {
        val windows = mutableSetOf<String>()
        for (rootObjet in rootObjects) {
            if (rootObjet is JavaRoot) {
                val iterator = rootObjet.eAllContents()
                while (iterator.hasNext()) {
                    val next = iterator.next()
                    if (next is tools.mdsd.jamopp.model.java.classifiers.Class || next is tools.mdsd.jamopp.model.java.classifiers.Interface || next is tools.mdsd.jamopp.model.java.classifiers.Enumeration) {
                        windows.add(next.name)
                    }
                }
            }
        }
        return windows
    }

    override fun getDisplayContent(): DisplayContentMapper<String> {
        return TextDisplayContentMapper()
    }


}