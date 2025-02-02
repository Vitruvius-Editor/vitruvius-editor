package tools.vitruv.vitruvAdapter.core.impl.selector

import tools.vitruv.framework.views.ViewSelector
import tools.vitruv.vitruvAdapter.core.api.Selector

/**
 * A selector that selects the selectableElements by their name.
 *
 * @property name The name to select by.
 * @property exact Whether the matching should be exact or also a substring
 */
class NameSelector(
    var name: String,
    var exact: Boolean,
) : Selector {
    override fun applySelection(viewSelector: ViewSelector) {
        TODO("Not yet implemented")
    }
}
