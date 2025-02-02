//package tools.vitruv.vitruvAdapter.core.api
//import org.eclipse.emf.ecore.EClass
//import org.eclipse.emf.ecore.EObject
//import org.eclipse.emf.ecore.EcoreFactory
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import tools.vitruv.vitruvAdapter.core.api.testutils.JavaClassViewMapper
//import tools.vitruv.vitruvAdapter.core.api.testutils.JsonNormalizer
//
//class CreateWindowContentMockupTest {
//
//    private lateinit var eObjects: List<EObject>
//
//    @BeforeEach
//    fun initEObjects() {
//        val testEClass = EcoreFactory.eINSTANCE.createEClass()
//        testEClass.name = "EClass"
//        testEClass.eStructuralFeatures.add(EcoreFactory.eINSTANCE.createEAttribute().apply {
//            name = "test"
//            eType = EcoreFactory.eINSTANCE.createEDataType().apply {
//                instanceClassName = "int"
//            }
//        })
//
//        val testEClass2 = EcoreFactory.eINSTANCE.createEClass()
//        testEClass2.name = "EClass2"
//        testEClass2.eStructuralFeatures.add(EcoreFactory.eINSTANCE.createEAttribute().apply {
//            name = "test2"
//            eType = EcoreFactory.eINSTANCE.createEDataType().apply {
//                instanceClassName = "char"
//            }
//        })
//        eObjects = listOf(testEClass, testEClass2)
//
//    }
//
//
//    @Test
//    fun testViewAndContentMapping() {
//        // Given: A JavaClassViewMapper
//        val mapper = JavaClassViewMapper()
//
//        // When: We map the EObjects to Windows
//        val windows = mapper.mapEObjectsToWindowsContent(eObjects)
//
//        // Then: We should get our Windows with the correct content
//        assertEquals(2, windows.size)
//        assertEquals("EClass", windows[0].name)
//        assertEquals("EClass2", windows[1].name)
//        assertEquals("public class EClass {\ntest: int\n}", windows[0].content)
//        assertEquals("public class EClass2 {\ntest2: char\n}", windows[1].content)
//
//        val jsonViewInformation = JsonViewInformation(mapper.getDisplayContent())
//        val serializedJson = jsonViewInformation.parseWindowsToJson(windows)
//        val expectedJson = """
//        {
//          "visualizerName": "TestVisualizer",
//          "windows": [
//            { "name": "EClass", "content": "public class EClass {\ntest: int\n}"},
//            { "name": "EClass2", "content": "public class EClass2 {\ntest2: char\n}"}
//          ]
//        }
//        """.trimIndent()
//        assertEquals(
//            JsonNormalizer.normalize(expectedJson),
//            JsonNormalizer.normalize(serializedJson)
//        )
//
//    }
//
//    @Test
//    fun testViewAndContentBackwardsMapping() {
//        val mapper = JavaClassViewMapper()
//        val expectedJson = """
//        {
//          "visualizerName": "TestVisualizer",
//          "windows": [
//            { "name": "EClass", "content": "public class EClass {\ntest: int\n}"},
//            { "name": "EClass2", "content": "public class EClass2 {\ntest2: char\n}"}
//          ]
//        }
//        """.trimIndent()
//        val jsonViewInformation = JsonViewInformation(mapper.getDisplayContent())
//        val contents = jsonViewInformation.parseWindowsFromJson(expectedJson)
//        val retrievedEObjects = mapper.mapWindowsContentToEObjects(contents)
//        assertEquals(2, retrievedEObjects.size)
//        assertEquals("EClass", (retrievedEObjects[0] as EClass).name)
//        assertEquals("EClass2", (retrievedEObjects[1] as EClass).name)
//        assertEquals(1, (retrievedEObjects[0] as EClass).eStructuralFeatures.size)
//        assertEquals(1, (retrievedEObjects[1] as EClass).eStructuralFeatures.size)
//        assertEquals("test", (retrievedEObjects[0] as EClass).eStructuralFeatures[0].name)
//        assertEquals("int", (retrievedEObjects[0] as EClass).eStructuralFeatures[0].eType.instanceClassName)
//        assertEquals("test2", (retrievedEObjects[1] as EClass).eStructuralFeatures[0].name)
//        assertEquals("char", (retrievedEObjects[1] as EClass).eStructuralFeatures[0].eType.instanceClassName)
//
//    }
//}