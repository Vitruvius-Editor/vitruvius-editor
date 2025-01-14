package tools.vitruv.vitruvAdapter.vitruv.api

import tools.vitruv.framework.views.View
import tools.vitruv.framework.views.ViewSelector
import tools.vitruv.framework.views.ViewType

/**
 * This interface represents a display view, which can be displayed in the Vitruvius graphical editor.
 * @author uhsab
 */
interface DisplayView {
    val name: String
    val viewType: ViewType<out ViewSelector>
    val viewMapper: ViewMapper
    val windowSelector: Selector
    val contentSelector: Selector
}
