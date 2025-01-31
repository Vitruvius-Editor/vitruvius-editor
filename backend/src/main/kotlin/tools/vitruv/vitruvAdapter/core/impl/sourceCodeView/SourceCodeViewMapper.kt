package tools.vitruv.vitruvAdapter.core.impl.sourceCodeView

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import org.eclipse.emf.ecore.EObject
import org.eclipse.uml2.uml.*
import tools.mdsd.jamopp.model.java.containers.JavaRoot
import tools.mdsd.jamopp.printer.JaMoPPPrinter
import tools.vitruv.vitruvAdapter.core.api.DisplayContentMapper
import tools.vitruv.vitruvAdapter.core.api.Window
import tools.vitruv.vitruvAdapter.core.impl.displayContentMapper.TextDisplayContentMapper
import tools.vitruv.vitruvAdapter.core.impl.mapper.TextViewMapper
import java.io.ByteArrayOutputStream
import java.util.function.Consumer

/**
 * This class maps java classes to a string source code representation
 */
class SourceCodeViewMapper: TextViewMapper() {

    override fun  mapEObjectsToWindowsContent(rootObjects: List<EObject>): List<Window<String>> {
        val windows = mutableSetOf<Window<String>>()

        for(classifier in rootObjects){
            if(classifier is tools.mdsd.jamopp.model.java.classifiers.ConcreteClassifier){
                val outputStream: ByteArrayOutputStream = ByteArrayOutputStream()
                val printer = JaMoPPPrinter.print(classifier, outputStream)
                val window = Window(classifier.name, outputStream.toString())
                windows.add(window)
            }
        }
        return windows.toList()
    }

    /**
     * Maps the given json string to view content, compares it to [oldEObjects] and applies the changes to [oldEObjects].
     * Note that no changes will be applied to the model,
     * this have to be done after this method with a View object, where the [oldEObjects] came from.
     * @param oldEObjects The old EObjects to compare the windows to.
     * @param windows the windows to map to EObjects.
     * @return The view content.
     */
    override fun mapWindowsToEObjectsAndApplyChangesToEObjects(
        oldEObjects: List<EObject>,
        windows: List<Window<String>>
    ): List<EObject> {
        TODO("Not yet implemented")
    }


    override fun mapViewToWindows(rootObjects: List<EObject>): Set<String> {
        val windows = mutableSetOf<String>()
        for (rootObjet in rootObjects){
            if(rootObjet is JavaRoot){
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