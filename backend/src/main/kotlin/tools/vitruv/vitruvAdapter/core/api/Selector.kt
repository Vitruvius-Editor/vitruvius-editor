package tools.vitruv.vitruvAdapter.core.api

import tools.vitruv.framework.views.ViewSelector

/**
 * A selector that selects elements within a viewSelector.
 * This is an internal-selector that is used to select elements in the rootObjects of a view.
 */
interface Selector {
    /**
     * Applies a selection to viewSelector.selectableElements.
     * @param viewSelector the viewSelector to select the elements within
     */
    fun applySelection(viewSelector: ViewSelector)
}
