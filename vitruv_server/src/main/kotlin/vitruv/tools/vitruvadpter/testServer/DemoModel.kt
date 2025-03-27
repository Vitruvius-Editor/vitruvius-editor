package vitruv.tools.vitruvadpter.testServer

import org.eclipse.emf.common.util.BasicEList
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EObject
import org.eclipse.uml2.uml.Type
import org.eclipse.uml2.uml.UMLFactory
import org.eclipse.uml2.uml.VisibilityKind
import org.eclipse.uml2.uml.internal.impl.LiteralIntegerImpl
import tools.mdsd.jamopp.model.java.classifiers.ClassifiersFactory
import tools.mdsd.jamopp.model.java.containers.ContainersFactory
import tools.mdsd.jamopp.model.java.literals.LiteralsFactory
import tools.mdsd.jamopp.model.java.members.MembersFactory
import tools.mdsd.jamopp.model.java.parameters.ParametersFactory
import tools.mdsd.jamopp.model.java.statements.StatementsFactory
import tools.mdsd.jamopp.model.java.types.TypesFactory
import tools.vitruv.applications.util.temporary.java.JavaMemberAndParameterUtil
import tools.vitruv.applications.util.temporary.java.JavaVisibility
import java.math.BigInteger

class DemoModel {
    companion object {
        fun createJavaModel(): EObject {
            val root = ClassifiersFactory.eINSTANCE.createClass()
            root.name = "Class1"
            root.makePublic()
            val member = MembersFactory.eINSTANCE.createField()
            member.name = "myIntAttribute"
            root.members.add(member)

            val intType = TypesFactory.eINSTANCE.createInt()

            val booleanType = TypesFactory.eINSTANCE.createBoolean()
            member.typeReference = intType

            val member1 = MembersFactory.eINSTANCE.createField()
            member1.name = "myBooleanAttribute"
            root.members.add(member1)
            val intType1 = TypesFactory.eINSTANCE.createInt()
            member1.typeReference = booleanType
            val initialValue1 = LiteralsFactory.eINSTANCE.createBooleanLiteral()
            initialValue1.isValue = true
            member1.initialValue = initialValue1

            val method = JavaMemberAndParameterUtil.createJavaClassMethod("myMethod", null, JavaVisibility.PUBLIC, false, false, null)
            val methodBlock = StatementsFactory.eINSTANCE.createBlock()
            method.statement = methodBlock
            root.members.add(method)

            val newClass = ClassifiersFactory.eINSTANCE.createClass()
            newClass.name = "Class2"
            root.makePublic()

            val member2 = MembersFactory.eINSTANCE.createField()
            member2.name = "myIntAttribute2"
            newClass.members.add(member2)
            val intType2 = TypesFactory.eINSTANCE.createInt()
            member2.typeReference = intType2

            val member3 = MembersFactory.eINSTANCE.createField()
            member3.name = "myIntAttribute3"
            newClass.members.add(member3)
            val intType3 = TypesFactory.eINSTANCE.createInt()
            member3.typeReference = intType3

            val initialValue = LiteralsFactory.eINSTANCE.createDecimalIntegerLiteral()
            initialValue.decimalValue = BigInteger.valueOf(5)
            member2.initialValue = initialValue

            val method2 = MembersFactory.eINSTANCE.createClassMethod()
            method2.name = "myMethod"
            method2.makePublic()
            method2.typeReference = TypesFactory.eINSTANCE.createInt()
            val parameter = ParametersFactory.eINSTANCE.createCatchParameter()
            parameter.name = "myParameter"
            parameter.typeReference = TypesFactory.eINSTANCE.createInt()
            method2.parameters.add(parameter)

            val block = StatementsFactory.eINSTANCE.createBlock()
            val statement = StatementsFactory.eINSTANCE.createReturn()
            val value = LiteralsFactory.eINSTANCE.createDecimalIntegerLiteral()
            value.decimalValue = BigInteger.valueOf(5)
            statement.returnValue = value
            method2.statement = block
            method2.block.statements.add(statement)
            root.members.add(method2)

            val javaPackage = ContainersFactory.eINSTANCE.createCompilationUnit()
            javaPackage.name = "exampleCompilationUnit"
            javaPackage.classifiers.add(root)
            javaPackage.classifiers.add(newClass)
            return javaPackage

        }
        fun createUmlModel(): EObject {
            val factory = UMLFactory.eINSTANCE
            val examplePackage = factory.createPackage()
            examplePackage.name = "examplePackage"

            val subPackage = examplePackage.createNestedPackage("innerPackage")
            val otherSubPackage = examplePackage.createNestedPackage("otherInnerPackage")

            val subPackageClass = subPackage.createOwnedClass("InnerClass", true)
            subPackageClass.createPackageImport(otherSubPackage)

            val umlClass = examplePackage.createOwnedClass("Class1", false)


            val class2 = examplePackage.createOwnedClass("Class2", true)
            val intatt = class2.createOwnedAttribute("myIntAttribute", null)
            class2.createOwnedOperation("myOperation", null, null)

            val intType = factory.createPrimitiveType()
            intType.name = "int"

            val initialValue2 = factory.createLiteralInteger()
            (initialValue2 as LiteralIntegerImpl).value = 5
            intatt.defaultValue = initialValue2

            val class1att = umlClass.createOwnedAttribute("myIntAttribute", intType)

            val initialValue1 = factory.createLiteralInteger()
            (initialValue2 as LiteralIntegerImpl).value = 20
            class1att.defaultValue = initialValue2

            examplePackage.packagedElements.add(intType)

            umlClass.superClasses.add(class2)

            val attribute = umlClass.createOwnedAttribute("myIntAttribute", null)
            attribute.visibility = VisibilityKind.PUBLIC_LITERAL

            val operationParameterNames: EList<String> = BasicEList<String>()
            operationParameterNames.add("param1")
            operationParameterNames.add("param2")

            val operationParameterTypes: EList<Type> = BasicEList<Type>()
            operationParameterTypes.add(intType)
            operationParameterTypes.add(intType)

            val operation = umlClass.createOwnedOperation("myOperation", operationParameterNames, operationParameterTypes)
            operation.type = intType

            // add body to operation
            val body = factory.createOpaqueBehavior()
            body.name = "getWindowsBody"
            body.languages.add("Kotlin")
            body.bodies.add(
                """
            System.out.println("Hello World");
            """.trimIndent(),
            )


            //add an interface
            val interface1 = examplePackage.createOwnedInterface("Interface1")
            val interface2 = examplePackage.createOwnedInterface("Interface2")
            interface1.createOwnedAttribute("myIntAttribute", intType)
            interface2.createOwnedAttribute("myIntAttribute", intType)
            interface1.createOwnedOperation("myOperation", operationParameterNames, operationParameterTypes)
            interface2.createOwnedOperation("myOperation", operationParameterNames, operationParameterTypes)
            interface1.redefinedInterfaces.add(interface2)
            return examplePackage

        }
    }

}