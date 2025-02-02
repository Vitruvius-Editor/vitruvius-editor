package tools.vitruv.vitruvAdapter.core.impl.displayContentMapper

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import tools.vitruv.vitruvAdapter.core.api.DisplayContentMapper
import tools.vitruv.vitruvAdapter.core.impl.VisualizerType
import tools.vitruv.vitruvAdapter.core.impl.classTableView.Table

/**
 * This class is used to map the content of a [Table] window to a string that can be displayed with the visualizer in the frontend and vice versa.
 * @param P the type of the content
 */

class TableDisplayContentMapper<P> @PublishedApi internal constructor(
    private val typeReference:
    TypeReference<Table<P>>
) : DisplayContentMapper<Table<P>> {

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    companion object {
        /**
         * Factory method to create an instance of TableDisplayContentMapper with type information.
         */
        inline fun <reified P> create(): TableDisplayContentMapper<P> {
            val typeRef = object : TypeReference<Table<P>>() {}
            return TableDisplayContentMapper(typeRef)
        }
    }

    /**
     * This function is used to parse the content of a window to a string.
     * @param content the content of the window
     * @return the string representation of the content
     */
    override fun parseContent(content: Table<P>): String {
        return objectMapper.writeValueAsString(content)
    }

    /**
     * This function is used to parse the string representation of the content to the content itself.
     * @param content the string representation of the content
     * @return the content itself
     */
    override fun parseString(content: String): Table<P> {
        return objectMapper.readValue(content, typeReference)
    }

    /**
     * This function is used to get the name of the visualizer that is used to display the content.
     * Note that Visualizer and ContentMapper should have a 1:1 relationship.
     * @return the name of the visualizer
     */
    override fun getVisualizerName(): String {
        return VisualizerType.ClassTableView.visualizerName
    }


}