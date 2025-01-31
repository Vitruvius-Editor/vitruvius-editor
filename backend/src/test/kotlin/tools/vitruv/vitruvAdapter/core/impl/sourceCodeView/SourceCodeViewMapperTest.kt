package tools.vitruv.vitruvAdapter.core.impl.sourceCodeView

import org.eclipse.emf.common.util.BasicEList
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EObject
import org.eclipse.uml2.uml.*
import org.eclipse.uml2.uml.internal.impl.LiteralIntegerImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class SourceCodeViewMapperTest {
    private lateinit var eObjects: List<EObject>

    @BeforeEach
    fun initObjects() {
        val factory = UMLFactory.eINSTANCE

        val examplePackage = factory.createPackage()
        examplePackage.name = "examplePackage"

        val umlClass = examplePackage.createOwnedClass("ExampleModel", false)

        val attribute = umlClass.createOwnedAttribute("myIntAttribute", null)
        attribute.visibility = VisibilityKind.PUBLIC_LITERAL

        val intType = factory.createDataType()
        intType.name = "int"
        attribute.type = intType
        attribute.setIsStatic(true)
        // 4.3 Set the Initial Value
        val initialValue = factory.createLiteralInteger()
        (initialValue as LiteralIntegerImpl).value = 5
        attribute.defaultValue = initialValue

       //create new attribute String myStringAttribute = "Hello"
        val stringType = factory.createDataType()
        stringType.name = "String"
        val attributeString = umlClass.createOwnedAttribute("myStringAttribute", stringType)
        attributeString.visibility = VisibilityKind.PUBLIC_LITERAL
        attributeString.setIsStatic(true)
        val initialValueString = factory.createLiteralString()
        (initialValueString as LiteralString).value = "Hello"
        attributeString.defaultValue = initialValueString


        //create not static attribute
        val attribute2 = umlClass.createOwnedAttribute("myIntAttribute2", intType)
        attribute2.visibility = VisibilityKind.PUBLIC_LITERAL
        attribute2.setIsStatic(false)
        val initialValue2 = factory.createLiteralInteger()
        (initialValue2 as LiteralIntegerImpl).value = 5
        attribute2.defaultValue = initialValue2

        val operationParameterNames: EList<String> = BasicEList<String>()
        operationParameterNames.add("param1")
        operationParameterNames.add("param2")

        val operationParameterTypes: EList<Type> = BasicEList<Type>()
        operationParameterTypes.add(intType)
        operationParameterTypes.add(intType)

        val operation = umlClass.createOwnedOperation("myOperation", operationParameterNames, operationParameterTypes)
        operation.type = stringType

        //add body to operation
        val body = factory.createOpaqueBehavior()
        body.name = "getWindowsBody"
        body.languages.add("Kotlin")
        body.bodies.add(
            """
            System.out.println("Hello World");
        """.trimIndent()
        )

        operation.methods.add(body)
        eObjects = listOf(umlClass)
    }

    @Test
    fun testMapEObjectsToWindowsContent() {
        val mapper = SourceCodeViewMapper()
        val windows = mapper.mapEObjectsToWindowsContent(eObjects)
        assertEquals(1, windows.size)
        assertEquals("ExampleModel", windows[0].name)
        print(windows[0].content)
    }

    @Test
    fun testMapWindowContentToEObjects() {
        val mapper = SourceCodeViewMapper()
        val windows = mapper.mapEObjectsToWindowsContent(this.eObjects)
    }

}