package tools.vitruv.vitruvAdapter.core.api

/**
 * This interface represents a display view, which can be displayed in the Vitruvius graphical editor.
 * @author uhsab
 */
interface DisplayView {
    /**
     * The name of the display view.
     */
    val name: String
    /**
     * The name of the viewType for the displayView.
     */
    val viewTypeName: String
    /**
     * The view mapper for the display view.
     */
    val viewMapper: ViewMapper<Any?>
    /**
     * The internal selector for the display view.
     * This selector is used to filter elements needed for the display view.
     */
    val internalSelector: Selector
    /**
     * The content selector for the display view.
     * This selector is used to select the content for the display view, for specific windows.
     */
    val contentSelector: ContentSelector
}


