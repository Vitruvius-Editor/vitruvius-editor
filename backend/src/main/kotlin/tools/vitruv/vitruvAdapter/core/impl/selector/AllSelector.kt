package tools.vitruv.vitruvAdapter.core.impl.selector

import tools.vitruv.framework.views.ViewSelector
import tools.vitruv.vitruvAdapter.core.api.Selector

/**
 * A selector that selects all selectable elements within a viewSelector.
 *
 */
class AllSelector : Selector {
    override fun applySelection(viewSelector: ViewSelector) {
        viewSelector.selectableElements.forEach{ viewSelector.setSelected(it, true)}
    }
}
