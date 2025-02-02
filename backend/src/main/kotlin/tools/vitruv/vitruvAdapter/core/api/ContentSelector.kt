package tools.vitruv.vitruvAdapter.core.api

import org.eclipse.emf.ecore.EObject
import tools.vitruv.framework.views.View

/**
 * The content selector selects the elements that are contained in the windows.
 */
interface ContentSelector {

    /**
     * Selects the view elements that are needed for the windows.
     * @param rootObjects the root objects of the view
     * @param windows the elements to select
     * @return the selected elements
     */
    fun applySelection(rootObjects: List<EObject>, windows: Set<String>) : List<EObject>
}