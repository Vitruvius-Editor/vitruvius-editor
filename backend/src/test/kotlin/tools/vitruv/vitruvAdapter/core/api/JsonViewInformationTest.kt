package tools.vitruv.vitruvAdapter.core.api

import org.junit.jupiter.api.Assertions.*
import tools.vitruv.vitruvAdapter.core.api.testutils.JsonNormalizer
import tools.vitruv.vitruvAdapter.core.api.testutils.TestTextDisplayContentMapper

class JsonViewInformationTest {

    
    @org.junit.jupiter.api.Test
    fun testTextMappingParseWindowsToJsonViewInformation() {
        val displayContentMapper = TestTextDisplayContentMapper()
        val viewInformation = JsonViewInformation(displayContentMapper)
        val window = Window("window1", "content1")
        val window2 = Window("window2", "content2")


        val expectedJson = """
        {
          "visualizerName": "TestVisualizer",
          "windows": [
            { "name": "window1", "content": "content1" },
            { "name": "window2", "content": "content2" }
          ]
        }
        """.trimIndent()

        val serializedJson = viewInformation.parseWindowsToJson(listOf(window,window2))/* Your JSON serialization method here */

        println(JsonNormalizer.normalize(serializedJson))

        // Compare the normalized strings
        assertEquals(JsonNormalizer.normalize(expectedJson), JsonNormalizer.normalize(serializedJson))
    }



}


