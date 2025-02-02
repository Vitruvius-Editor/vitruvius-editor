package tools.vitruv.vitruvAdapter.core.api

/**
 * This class is used to map the content of a window to a string that can be displayed with the visualizer in the frontend and vice versa.
 *
 * @author uhsab
 */
interface DisplayContentMapper<E> {


    /**
     * This function is used to parse the content of a window to a string.
     * @param content the content of the window
     * @return the string representation of the content
     */
    fun parseContent(content: E): String

    /**
     * This function is used to parse the string representation of the content to the content itself.
     * @param content the string representation of the content
     * @return the content itself
     */
    fun parseString(content: String): E

    /**
     * This function is used to get the name of the visualizer that is used to display the content.
     * Note that Visualizer and ContentMapper should have a 1:1 relationship.
     * @return the name of the visualizer
     */
    fun getVisualizerName(): String
}
