package tools.vitruv.vitruvAdapter.core.impl.classTableView

import org.eclipse.emf.ecore.EObject
import org.eclipse.uml2.uml.Package
import org.eclipse.uml2.uml.UMLFactory
import tools.vitruv.framework.views.View
import tools.vitruv.vitruvAdapter.core.api.ContentSelector
import org.eclipse.uml2.uml.Class

class ClassTableContentSelector: ContentSelector {

    override fun applySelection(
        rootObjects: List<EObject>,
        windows: Set<String>
    ): List<EObject> {
        val rootObjectsWithClassOnly = mutableListOf<EObject>()
        for (ePackage in rootObjects) {
            if(ePackage is Package){
                val iterator = ePackage.eAllContents()
                while (iterator.hasNext()) {
                    val next = iterator.next()
                    if (next is Package) {
                        rootObjectsWithClassOnly.add(packageWithClassOnly(next))
                    }
                }
                rootObjectsWithClassOnly.add(packageWithClassOnly(ePackage))
            }
        }
        return rootObjectsWithClassOnly
    }


    private fun packageWithClassOnly(ePackage: Package): Package {
        val newPackage = UMLFactory.eINSTANCE.createPackage()
        newPackage.name = ePackage.name

        // Create a copy of the elements before iteration
        val elementsCopy = ePackage.packagedElements.toList()

        for (element in elementsCopy) {
            if (element is Class) {
                newPackage.packagedElements.add(element)
            }
        }

        return newPackage
    }
}