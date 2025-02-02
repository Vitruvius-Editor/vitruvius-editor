package tools.vitruv.vitruvAdapter.core.impl.sourceCodeView

import org.eclipse.emf.ecore.EObject
import tools.mdsd.jamopp.model.java.containers.JavaRoot
import tools.vitruv.framework.views.View
import tools.vitruv.vitruvAdapter.core.api.ContentSelector


/**
 * The windows in a source code view are classes, interfaces and enumerations.
 * This class selects the classes, interfaces and enumerations that are in the windows from the view.
 * Based on the Java Metamodel.
 */

class SourceCodeContentSelector : ContentSelector {
    override fun applySelection(rootObjects: List<EObject> , windows: Set<String>): List<EObject> {
        val selectedElements = mutableListOf<EObject>()
        for (rootObject in rootObjects) {
            if(rootObject is JavaRoot){
                val iterator = rootObject.eAllContents()
                while (iterator.hasNext()) {
                    val next = iterator.next()
                    if (next is tools.mdsd.jamopp.model.java.classifiers.Class|| next is tools.mdsd.jamopp.model.java.classifiers.Interface || next is tools.mdsd.jamopp.model.java.classifiers.Enumeration) {
                        if (windows.contains(next.name)) {
                            selectedElements.add(next)
                        }
                    }
                }
            }
        }
        return selectedElements
    }
}