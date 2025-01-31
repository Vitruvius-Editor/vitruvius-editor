package tools.vitruv.vitruvAdapter.core.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature

/**
 * This class is responsible for serializing the visualizer information and the list of windows to a json string.
 * @author uhsab
 */
class JsonViewInformation<E>(
    private val displayContentMapper: DisplayContentMapper<E>,
) {

    /**
     * Serializes the visualizer information and the list of windows to a json string.
     * @param windows The list of windows.
     * @return The generated json string.
     */
    fun parseWindowsToJson(windows: List<Window<E>>): String {
        // Map each Window to a SerializableWindow by parsing its content.
        val serializableWindows = windows.map { window ->
            SerializableWindow(
                name = window.name,
                content = displayContentMapper.parseContent(window.content)
            )
        }

        // Create a data structure to hold the full JSON content.
        val output = mapOf(
            "visualizerName" to displayContentMapper.getVisualizerName(),
            "windows" to serializableWindows
        )

        val objectMapper = ObjectMapper().apply {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
        return objectMapper.writeValueAsString(output)
    }

    /**
     * Deserializes the given json string to a list of windows.
     * @param json The json string to deserialize.
     * @return The list of windows.
     */
    fun parseWindowsFromJson(json: String): List<Window<E>> {
        val objectMapper = ObjectMapper()
        val jsonNode = objectMapper.readTree(json)
        val windows = jsonNode.get("windows").map { windowNode ->
            val name = windowNode.get("name").asText()
            val content = displayContentMapper.parseString(windowNode.get("content").asText())
            Window(name, content)
        }
        return windows
    }


    /**
     * Deserializes the given json string to a list of window names.
     * @param json The json string to deserialize.
     * @return The list of window names.
     */
    fun collectWindowsFromJson(json: String): Set<String> {
        val objectMapper = ObjectMapper()
        val jsonNode = objectMapper.readTree(json)
        return jsonNode.get("windows").map { windowNode ->
            windowNode.get("name").asText()
        }.toSet()
    }

}
