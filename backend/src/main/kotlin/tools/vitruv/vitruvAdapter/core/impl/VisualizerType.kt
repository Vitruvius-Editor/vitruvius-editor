package tools.vitruv.vitruvAdapter.core.impl


/**
 * Enum class for the different visualizer types.
 * These are the visualizers that the Vitruvius graphical editor supports.
 * @param visualizerName The name of the visualizer.
 */
enum class VisualizerType(val visualizerName: String) {
    TEXT_VISUALIZER("TextVisualizer"),
    TABLE_VISUALIZER("TableVisualizer"),
    UML_VISUALIZER("UmlVisualizer")
}