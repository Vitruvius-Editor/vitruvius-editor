package tools.vitruv.vitruvAdapter.core.api

import org.eclipse.emf.ecore.EObject

/**
 * This interface represents a view mapper, which is able to map contents of a view (RootObjects/EObjects),
 * to Windows that can be displayed in the graphical editor.
 * These are represented as json contents, which the graphical editor can interpret.
 * @author uhsab
 */
interface ViewMapper<E> {

    /**
     * Maps the given view content to a json string, which can be displayed in the graphical editor.
     * @param selectEObjects The view content to map.
     * @return The json string representing the view content.
     */
    fun mapEObjectsToWindowsContent(selectEObjects: List<EObject>): List<Window<E>>


    /**
     * Maps the given json string to view content, compares it to [oldEObjects] and applies the changes to [oldEObjects].
     * Note that no changes will be applied to the model,
     * this have to be done after this method with a View object, where the [oldEObjects] came from.
     * @param oldEObjects The old EObjects to compare the windows to.
     * @param windows the windows to map to EObjects.
     * @return The view content.
     */
    fun mapWindowsToEObjectsAndApplyChangesToEObjects(oldEObjects: List<EObject>, windows: List<Window<E>>): List<EObject>


    /**
     * Maps the given view to all windows it can find within the view.
     * @param rootObjects The view to map.
     * @return The names of the windows that are available in the view.
     */
    fun mapViewToWindows(rootObjects: List<EObject>): Set<String>

    /**
     * Gets the display content of this view mapper, which is able to map the view content to a json string and vice versa.
     * @return The display content of this view mapper.
     */
    fun getDisplayContent(): DisplayContentMapper<E>
}
