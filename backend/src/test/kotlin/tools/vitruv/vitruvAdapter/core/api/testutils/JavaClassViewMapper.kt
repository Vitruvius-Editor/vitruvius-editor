//package tools.vitruv.vitruvAdapter.core.api.testutils
//
//import org.eclipse.emf.ecore.EObject
//import org.eclipse.emf.ecore.EcoreFactory
//import org.eclipse.emf.ecore.impl.EClassImpl
//import tools.vitruv.vitruvAdapter.core.api.DisplayContentMapper
//import tools.vitruv.vitruvAdapter.core.api.ViewMapper
//import tools.vitruv.vitruvAdapter.core.api.Window
//
///**
// * A mock-up mapper that takes a list of MockJavaClass EObjects
// * and turns them into Windows with simple String content.
// */
//class JavaClassViewMapper : ViewMapper<String> {
//
//    private val displayContentMapper = TestTextDisplayContentMapper()
//
//    override fun mapEObjectsToWindowsContent(rootObjects: List<EObject>): List<Window<String>> {
//        return rootObjects.map {
//            if (it !is EClassImpl) {
//                throw IllegalArgumentException("Only EClasses are supported")
//            }
//            val eClass = it
//            val className = eClass.name
//            val attributes = eClass.eStructuralFeatures.joinToString("\n") { attr -> "${attr.name}: ${attr.eType.instanceClass.simpleName}" }
//            Window(name = className, content = "public class $className {\n$attributes\n}")
//        }
//    }
//
//    override fun mapWindowsContentToEObjects(windows: List<Window<String>>): List<EObject> {
//        return windows.map {
//            val className = it.name
//            val attributes = it.content.lines().drop(1).dropLast(1).map { line ->
//                val parts = line.split(":")
//                EcoreFactory.eINSTANCE.createEAttribute().apply {
//                    name = parts[0].trim()
//                    eType = EcoreFactory.eINSTANCE.createEDataType().apply { instanceClassName = parts[1].trim() }
//                }
//            }
//            EcoreFactory.eINSTANCE.createEClass().apply {
//                name = className
//                eStructuralFeatures.addAll(attributes)
//            }
//        }
//    }
//
//    override fun mapViewToWindows(rootObjects: List<EObject>): Set<String> {
//        //Map EClasses to window names
//        return rootObjects.map { (it as EClassImpl).name }.toSet()
//
//    }
//
//    override fun getDisplayContent(): DisplayContentMapper<String> {
//       return displayContentMapper
//    }
//}