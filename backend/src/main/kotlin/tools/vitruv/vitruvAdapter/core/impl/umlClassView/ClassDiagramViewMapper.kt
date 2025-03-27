package tools.vitruv.vitruvAdapter.core.impl.umlClassView

import org.eclipse.emf.ecore.EObject
import org.eclipse.uml2.uml.*
import tools.vitruv.vitruvAdapter.core.api.DisplayContentMapper
import tools.vitruv.vitruvAdapter.core.api.PreMappedWindow
import tools.vitruv.vitruvAdapter.core.api.Window
import tools.vitruv.vitruvAdapter.core.impl.DisplayViewName
import tools.vitruv.vitruvAdapter.core.impl.EUtils
import tools.vitruv.vitruvAdapter.core.impl.ViewRecommendation
import tools.vitruv.vitruvAdapter.core.impl.abstractMapper.UmlViewMapper
import tools.vitruv.vitruvAdapter.core.impl.displayContentMapper.UmlDisplayContentMapper
import tools.vitruv.vitruvAdapter.core.impl.uml.*

/**
 * Maps EObjects from the UML metamodel to class diagrams and vice versa.
 * @see UmlViewMapper
 * @see UmlDiagram
 * @author
 */
class ClassDiagramViewMapper : UmlViewMapper() {
    private fun mapPackageToUmlDiagram(eObject: Package): UmlDiagram {
        val nodes = mutableListOf<UmlNode>()
        val connections = mutableListOf<UmlConnection>()
        val iterator = eObject.eAllContents()
        val resource = eObject.eResource()
        while (iterator.hasNext()) {
            val next = iterator.next()
            val nextURI = EUtils.getUUIDForEObject(next)
            if (next is Class) {
                val viewRecommendations = mutableListOf<ViewRecommendation>()
                viewRecommendations.add(ViewRecommendation(DisplayViewName.SOURCE_CODE.viewName, next.name))
                val isAbstract = if (next.isAbstract) "<<abstract>>" else "<<class>>"
                val classNode =
                    UmlNode(nextURI, next.name, isAbstract, getUmlAttributes(next), getUmlMethods(next), viewRecommendations.toList())
                nodes.add(classNode)

                if (next.interfaceRealizations.isNotEmpty()) {
                    next.interfaceRealizations.forEach { interfaceRealization ->
                        val sourceURI = EUtils.getUUIDForEObject(next)
                        val targetURI = EUtils.getUUIDForEObject(interfaceRealization.contract)
                        val umlConnection =
                            UmlConnection(
                                "$sourceURI$$targetURI",
                                sourceURI,
                                targetURI,
                                UmlConnectionType.IMPLEMENTS,
                                "",
                                "",
                                "",
                            )
                        connections.add(umlConnection)
                    }
                }
                if (next.superClasses.isNotEmpty()) {
                    val superClass = next.superClasses.first()
                    val sourceURI = EUtils.getUUIDForEObject(next)
                    val targetURI = EUtils.getUUIDForEObject(superClass)
                    val umlConnection =
                        UmlConnection(
                            "$sourceURI$$targetURI",
                            sourceURI,
                            targetURI,
                            UmlConnectionType.EXTENDS,
                            "",
                            "",
                            "",
                        )
                    connections.add(umlConnection)
                }
            }
            if (next is Interface) {
                val viewRecommendations = mutableListOf<ViewRecommendation>()
                viewRecommendations.add(ViewRecommendation(DisplayViewName.SOURCE_CODE.viewName, next.name))
                val interfaceNode =
                    UmlNode(nextURI, next.name, "<<interface>>", listOf(), getUmlMethods(next), viewRecommendations)
                nodes.add(interfaceNode)

                if (next.redefinedInterfaces.isNotEmpty()) {
                    next.redefinedInterfaces.forEach { redefinedInterface ->
                        val sourceURI = EUtils.getUUIDForEObject(next)
                        val targetURI = EUtils.getUUIDForEObject(redefinedInterface)
                        val umlConnection =
                            UmlConnection(
                                "$sourceURI$$targetURI",
                                sourceURI,
                                targetURI,
                                UmlConnectionType.EXTENDS,
                                "",
                                "",
                                "",
                            )
                        connections.add(umlConnection)
                    }
                }
            }
        }
        return UmlDiagram(nodes, connections)
    }

    private fun getUmlAttributes(next: Class): List<UmlAttribute> {
        val umlAttributes = mutableListOf<UmlAttribute>()
        next.ownedAttributes.forEach {
            val visibilitySymbol = getVisibilitySymbol(it.visibility?.literal?.lowercase() ?: UmlVisibility.PUBLIC.symbol)
            val umlVisibility = UmlVisibility.fromSymbol(visibilitySymbol) ?: UmlVisibility.PUBLIC
            umlAttributes.add(
                UmlAttribute(
                    EUtils.getUUIDForEObject(it),
                    umlVisibility,
                    it.name ?: "not_defined",
                    UmlType(EUtils.getUUIDForEObject(it.type), it.type?.name ?: "Object"),
                ),
            )
        }
        return umlAttributes
    }

    private fun getUmlMethods(next: Classifier): List<UmlMethod> {
        val umlMethods = mutableListOf<UmlMethod>()
        next.operations.forEach { operation ->
            val visibilitySymbol = getVisibilitySymbol(operation.visibility?.literal?.lowercase() ?: UmlVisibility.PUBLIC.symbol)
            val umlVisibility = UmlVisibility.fromSymbol(visibilitySymbol) ?: UmlVisibility.PUBLIC
            val umlParameters = mutableListOf<UmlParameter>()
            operation.ownedParameters.filter { it.direction == ParameterDirectionKind.IN_LITERAL }.forEach { parameter ->
                umlParameters.add(
                    UmlParameter(
                        EUtils.getUUIDForEObject(parameter),
                        parameter.name ?: "not_defined",
                        UmlType(
                            EUtils.getUUIDForEObject(parameter.type),
                            parameter.type?.name ?: "Object",
                        ),
                    ),
                )
            }
            umlMethods.add(
                UmlMethod(
                    EUtils.getUUIDForEObject(operation),
                    umlVisibility,
                    operation.name ?: "not_defined",
                    umlParameters,
                    UmlType(
                        EUtils.getUUIDForEObject(operation.type),
                        operation.type?.name ?: "Object",
                    ),
                ),
            )
        }
        return umlMethods
    }

    private fun getVisibilitySymbol(visibility: String): String =
        when (visibility) {
            "public" -> "+"
            "private" -> "-"
            "protected" -> "#"
            "package" -> "~"
            else -> ""
        }

    /**
     * Maps the given view content to a list of windows.
     * @param preMappedWindows the pre-mapped windows to map to windows.
     * @return The windows representing the view content.
     */
    override fun mapEObjectsToWindows(preMappedWindows: List<PreMappedWindow<UmlDiagram>>): List<Window<UmlDiagram>> {
        val windows = mutableListOf<Window<UmlDiagram>>()
        for (preMappedWindow in preMappedWindows) {
            for (eObject in preMappedWindow.neededEObjects) {
                if (eObject !is Package) {
                    continue
                }
                val umlDiagram = mapPackageToUmlDiagram(eObject)
                windows.add(preMappedWindow.createWindow(umlDiagram))
            }
        }

        return windows
    }

    /**
     * Maps the given json string to view content, compares it to [preMappedWindows] and applies the changes to the eObjects of [preMappedWindows].
     * Note that no changes will be applied to the model,
     * this have to be done after this method with a View object, where the [preMappedWindows] came from.
     * @param preMappedWindows The pre-mapped windows to compare the windows to.
     * @param windows the windows to map to EObjects.
     * @return The view content.
     */
    override fun applyWindowChangesToView(
        preMappedWindows: List<PreMappedWindow<UmlDiagram>>,
        windows: List<Window<UmlDiagram>>,
    ): List<EObject> {
        val windowPairs = pairWindowsTogether(preMappedWindows, windows)
        for (item in windowPairs) {
            applyChangesToWindow(item.first, item.second)
        }
        return listOf() // unnecessary return value, has to be changed
    }

    private fun applyChangesToWindow(
        preMappedWindow: PreMappedWindow<UmlDiagram>,
        window: Window<UmlDiagram>,
    ) {
        val checkedClasses = mutableListOf<PackageableElement>()
        for (eObject in preMappedWindow.neededEObjects) {
            if (eObject !is Package) {
                continue
            }
            checkedClasses.addAll(eObject.packagedElements.filterIsInstance<PackageableElement>())
            for (node in window.content.nodes) {
                val umlElement = eObject.eResource()?.getEObject(node.uuid)
                if (umlElement == null) {
                    if (node.nodeType == "<<class>>") {
                        val newClass = eObject.createOwnedClass(node.name, false)
                        newClass.name = node.name
                        node.attributes.forEach { createAttributeForUmlAttributeInClass(newClass, it) }
                        node.methods.forEach { createMethodFromUmlMethodInClass(newClass, it) }
                        eObject.packagedElements.add(newClass)
                    } else if (node.nodeType == "<<interface>>") {
                        val newInterface = eObject.createOwnedInterface(node.name)
                        newInterface.name = node.name
                        node.methods.forEach { createMethodFromUmlMethodInInterface(newInterface, it) }
                        eObject.packagedElements.add(newInterface)
                    }
                    continue
                }
                checkedClasses.remove(umlElement)
                val connections = findAllConnectionsForSourceCodeUUID(node.uuid, window.content.connections)

                for (connection in connections) {
                    if (connection.targetNodeUUID == "" || connection.sourceNodeUUID == "") {
                        throw IllegalStateException(
                            "The target node UUID of a connection of type EXTENDS is empty." +
                                "You cannot add connections between nodes wich have not been created in the model yet.",
                        )
                    }
                    val sourceClass = eObject.eResource()?.getEObject(connection.sourceNodeUUID)
                    val targetClass = eObject.eResource()?.getEObject(connection.targetNodeUUID)

                    if (sourceClass == null || targetClass == null) {
                        throw IllegalStateException("The source or target class of a connection could not be found in the model.")
                    }

                    if (connection.connectionType == UmlConnectionType.EXTENDS) {
                        if (targetClass is Class && sourceClass is Class) {
                            val alreadyGeneralized = sourceClass.generalizations.any { it.general == targetClass }
                            if (!alreadyGeneralized) {
                                sourceClass.createGeneralization(targetClass)
                            }
                        } else if (targetClass is Interface && sourceClass is Interface) {
                            // Check if generalization already exists
                            val alreadyGeneralized = sourceClass.generalizations.any { it.general == targetClass }
                            if (!alreadyGeneralized) {
                                sourceClass.createGeneralization(targetClass)
                            }
                        } else {
                            throw IllegalStateException("Extends can only be used between two classes or interfaces.")
                        }
                    } else if (connection.connectionType == UmlConnectionType.IMPLEMENTS) {
                        if (sourceClass is Class && targetClass is Interface) {
                            val alreadyImplemented = sourceClass.interfaceRealizations.any { it.contract == targetClass }

                            if (!alreadyImplemented) {
                                // Create the interface realization only if it does not exist.
                                sourceClass.createInterfaceRealization(connection.connectionName, targetClass)
                            }
                        } else {
                            throw IllegalStateException("Implements can only be used between a class and an interface.")
                        }
                    } else {
                        throw IllegalStateException("The connection type ${connection.connectionType} is not supported.")
                    }
                }

                if (umlElement is Class) {
                    if (umlElement.name == null || umlElement.name != node.name) {
                        umlElement.name = node.name
                    }
                    val checkedAttributes = mutableListOf<Property>()
                    checkedAttributes.addAll(umlElement.ownedAttributes)
                    for (umlAttribute in node.attributes) {
                        val attribute = umlElement.eResource().getEObject(umlAttribute.uuid)
                        if (attribute == null) {
                            createAttributeForUmlAttributeInClass(umlElement, umlAttribute)
                        } else {
                            if (attribute is Property) {
                                editAttributeProperties(attribute, umlAttribute, umlElement)
                            } else {
                                throw IllegalStateException(
                                    "The attribute with the UUID ${umlAttribute.uuid} could not be linked to a attribute.",
                                )
                            }
                            checkedAttributes.remove(attribute)
                        }
                    }

                    for (attribute in checkedAttributes) {
                        attribute.destroy()
                    }

                    val checkedMethods = mutableListOf<Operation>()
                    checkedMethods.addAll(umlElement.operations)
                    for (method in node.methods) {
                        val operation = umlElement.eResource().getEObject(method.uuid)

                        if (operation == null) {
                            createMethodFromUmlMethodInClass(umlElement, method)
                        } else {
                            if (operation is Operation) {
                                editOperationProperties(operation, method, umlElement)
                            } else {
                                throw IllegalStateException(
                                    "The operation with the UUID ${method.uuid} could not be linked to a operation.",
                                )
                            }
                            checkedMethods.remove(operation)
                        }
                    }
                    for (method in checkedMethods) {
                        method.destroy()
                    }
                    if (umlElement.superClasses.isNotEmpty()) {
                        val extendsConnection = connections.find { it.connectionType == UmlConnectionType.EXTENDS }
                        if (extendsConnection == null) {
                            umlElement.superClasses.clear()
                        }
                    }
                    val interfaceRealizationsToDelete = mutableListOf<InterfaceRealization>()
                    for (interfaceRealization in umlElement.interfaceRealizations) {
                        val implementsConnection =
                            connections.find {
                                it.connectionType == UmlConnectionType.IMPLEMENTS &&
                                    it.targetNodeUUID == EUtils.getUUIDForEObject(interfaceRealization.contract)
                            }
                        if (implementsConnection == null) {
                            interfaceRealizationsToDelete.add(interfaceRealization)
                        }
                    }
                    interfaceRealizationsToDelete.forEach { it.destroy() }
                } else if (umlElement is Interface) {
                    if (umlElement.name != node.name) {
                        umlElement.name = node.name
                    }

                    val checkedMethods = mutableListOf<Operation>()
                    checkedMethods.addAll(umlElement.ownedOperations)
                    for (method in node.methods) {
                        val umlOperation = umlElement.eResource().getEObject(method.uuid)
                        if (umlOperation == null) {
                            createMethodFromUmlMethodInInterface(umlElement, method)
                        } else {
                            if (umlOperation is Operation) {
                                editOperationProperties(umlOperation, method, umlElement)
                            } else {
                                throw IllegalStateException(
                                    "The operation with the UUID ${method.uuid} could not be linked to a operation.",
                                )
                            }
                            checkedMethods.remove(umlOperation)
                        }
                    }
                    for (method in checkedMethods) {
                        method.destroy()
                    }
                }
            }
        }
        for (checkedClass in checkedClasses) {
            if (checkedClass is Class || checkedClass is Interface) {
                checkedClass.destroy()
            }
        }
    }

    private fun editAttributeProperties(
        attribute: Property,
        umlAttribute: UmlAttribute,
        umlElement: Class,
    ) {
        editVisibility(attribute, umlAttribute.visibility)
        if (attribute.name == null || attribute.name != umlAttribute.name) {
            attribute.name = umlAttribute.name
        }
        if (EUtils.getUUIDForEObject(attribute.type) != umlAttribute.type.uuid) {
            val type = getTypeOrPrimitiveType(umlAttribute.type.name, umlElement.`package`)
            attribute.type = type
        }
    }

    private fun editVisibility(
        namedElement: NamedElement,
        visibility: UmlVisibility,
    ) {
        if (namedElement.visibility == null || namedElement.visibility.literal.lowercase() != visibility.symbol) {
            namedElement.visibility = VisibilityKind.get(visibility.symbol)
        }
    }

    private fun editOperationProperties(
        operation: Operation,
        umlMethod: UmlMethod,
        classifier: Classifier,
    ) {
        editVisibility(operation, umlMethod.visibility)
        if (operation.name == null || operation.name != umlMethod.name) {
            operation.name = umlMethod.name
        }
        if (operation.type == null || operation.type != getTypeOrPrimitiveType(umlMethod.returnType.name, classifier.`package`)) {
            val type = getTypeOrPrimitiveType(umlMethod.returnType.name, classifier.`package`)
            operation.type = type
        }
        val checkedParameters = mutableListOf<Parameter>()
        checkedParameters.addAll(operation.ownedParameters)
        for (parameter in umlMethod.parameters) {
            val ownedParameter = operation.eResource().getEObject(parameter.uuid)
            if (ownedParameter == null || ownedParameter !is Parameter) {
                operation.createOwnedParameter(parameter.name, getTypeOrPrimitiveType(parameter.type.name, classifier.`package`))
            } else {
                checkedParameters.remove(ownedParameter)
                if (ownedParameter.type == null ||
                    ownedParameter.type != getTypeOrPrimitiveType(parameter.type.name, classifier.`package`)
                ) {
                    ownedParameter.type = getTypeOrPrimitiveType(parameter.type.name, classifier.`package`)
                }

                if (ownedParameter.name == null || ownedParameter.name != parameter.name) {
                    ownedParameter.name = parameter.name
                }
            }
        }
        for (parameter in checkedParameters) {
            if (parameter.direction == ParameterDirectionKind.IN_LITERAL) {
                parameter.destroy()
            }
        }
    }

    private fun createMethodFromUmlMethodInClass(
        umlClass: Class,
        umlMethod: UmlMethod,
    ) {
        val newOperation = umlClass.createOwnedOperation(umlMethod.name, null, null, null)
        newOperation.name = umlMethod.name
        val type = getType(umlMethod.returnType.name, umlClass.`package`)
        newOperation.type = type
        for (parameter in umlMethod.parameters) {
            newOperation.createOwnedParameter(
                parameter.name,
                getTypeOrPrimitiveType(parameter.type.name, umlClass.`package`),
            )
        }
    }

    private fun createMethodFromUmlMethodInInterface(
        umlInterface: Interface,
        methods: UmlMethod,
    ) {
        val newOperation = umlInterface.createOwnedOperation(methods.name, null, null, null)
        newOperation.name = methods.name

        val type = getTypeOrPrimitiveType(methods.returnType.name, umlInterface.`package`)
        newOperation.type = type
    }

    private fun createAttributeForUmlAttributeInClass(
        umlClass: Class,
        umlAttribute: UmlAttribute,
    ) {
        val newAttribute = umlClass.createOwnedAttribute(umlAttribute.name, null)
        newAttribute.name = umlAttribute.name
        newAttribute.type = getTypeOrPrimitiveType(umlAttribute.type.name, umlClass.`package`)
    }

    private fun findAllConnectionsForSourceCodeUUID(
        sourceCodeUUID: String,
        connections: List<UmlConnection>,
    ): List<UmlConnection> =
        connections.filter {
            it.sourceNodeUUID == sourceCodeUUID
        }

    private fun getTypeOrPrimitiveType(
        typeName: String,
        umlPackage: Package,
    ): Type {
        val type = getType(typeName, umlPackage)
        if (type != null) {
            return type
        }
        return createPrimitiveTypeInPackage(umlPackage, typeName)
    }

    private fun createPrimitiveTypeInPackage(
        umlPackage: Package,
        typeName: String,
    ): Type {
        val type = UMLFactory.eINSTANCE.createPrimitiveType()
        type.name = typeName
        umlPackage.packagedElements.add(type)
        return type
    }

    private fun getType(
        typeName: String,
        umlPackage: Package,
    ): Type? {
        val type = umlPackage.getOwnedType(typeName)
        return type
    }

    /**
     * Maps the given view to all windows it can find within the view.
     * @param rootObjects The view to map.
     * @return The names of the windows that are available in the view.
     */
    override fun collectWindowsFromView(rootObjects: List<EObject>): Set<String> {
        val windows = mutableSetOf<String>()
        for (rootObject in rootObjects) {
            val iterator = rootObject.eAllContents()
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (next is Package) {
                    windows.add(next.name)
                }
            }
            if (rootObject is Package) {
                windows.add(rootObject.name)
            }
        }
        return windows
    }

    /**
     * Gets the display content of this view mapper, which is able to map the view content to a json string and vice versa.
     * @return The display content of this view mapper.
     */
    override fun getDisplayContentMapper(): DisplayContentMapper<UmlDiagram> = UmlDisplayContentMapper()
}
