package tools.vitruv.vitruvAdapter.core.impl.sourceCodeView

import org.eclipse.emf.ecore.EObject
import tools.mdsd.jamopp.model.java.containers.JavaRoot
import tools.vitruv.vitruvAdapter.core.api.ContentSelector
import tools.vitruv.vitruvAdapter.core.api.PreMappedWindow

/**
 * The windows in a source code view are classes, interfaces and enumerations.
 * This class selects the classes, interfaces and enumerations that are in the windows from the view.
 * Based on the Java Metamodel.
 */

class SourceCodeContentSelector : ContentSelector<String> {
    override fun applySelection(
        rootObjects: List<EObject>,
        windows: Set<String>,
    ): List<PreMappedWindow<String>> {
        val mutablePreMappedWindows = ContentSelector.createMutablePreMappedWindows(windows)
        for (rootObject in rootObjects) {
            if (rootObject is JavaRoot) {
                val iterator = rootObject.eAllContents()
                while (iterator.hasNext()) {
                    val next = iterator.next()
                    if (next is tools.mdsd.jamopp.model.java.classifiers.Class ||
                        next is tools.mdsd.jamopp.model.java.classifiers.Interface ||
                        next is tools.mdsd.jamopp.model.java.classifiers.Enumeration
                    ) {
                        if (windows.contains(next.name)) {
                            ContentSelector.findPreMappedWindow(mutablePreMappedWindows, next.name)?.addEObject(next)
                        }
                    }
                }
            }
        }
        return mutablePreMappedWindows.map { it.toPreMappedWindow() }
    }
}
