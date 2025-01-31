package tools.vitruv.vitruvAdapter.core.impl.classTableView

import org.eclipse.emf.ecore.EObject
import org.eclipse.uml2.uml.Class
import org.eclipse.uml2.uml.OpaqueBehavior
import org.eclipse.uml2.uml.Package
import org.eclipse.uml2.uml.UMLFactory
import tools.vitruv.vitruvAdapter.core.api.DisplayContentMapper
import tools.vitruv.vitruvAdapter.core.api.ViewMapper
import tools.vitruv.vitruvAdapter.core.api.Window
import tools.vitruv.vitruvAdapter.core.impl.displayContentMapper.TableDisplayContentMapper

class ClassTableViewMapper: ViewMapper<Table<ClassTableEntry>> {
    /**
     * Maps the given view content to a json string, which can be displayed in the graphical editor.
     * @param rootObjects The view content to map.
     * @return The json string representing the view content.
     */
    override fun mapEObjectsToWindowsContent(rootObjects: List<EObject>): List<Window<Table<ClassTableEntry>>> {
        val windows = mutableListOf<Window<Table<ClassTableEntry>>>()
        for (rootObject in rootObjects) {
            if (rootObject !is Package) {
                continue
            }
            val entries = mutableSetOf<ClassTableEntry>()
            rootObject.packagedElements.forEach { element ->
                if (element !is Class) {
                    return@forEach
                }
                entries.add(createClassEntryFromUmlClass(element))
            }
            val window = Window(rootObject.name, Table(entries))
            windows.add(window)
        }
        return windows
    }

    /**
     * Maps the given json string to view content, compares it to [oldEObjects] and applies the changes to [oldEObjects].
     * Note that no changes will be applied to the model,
     * this have to be done after this method with a View object, where the [oldEObjects] came from.
     * @param oldEObjects The old EObjects to compare the windows to.
     * @param windows the windows to map to EObjects.
     * @return The view content.
     */
    override fun mapWindowsToEObjectsAndApplyChangesToEObjects(
        oldEObjects: List<EObject>,
        windows: List<Window<Table<ClassTableEntry>>>
    ): List<EObject> {
        TODO("Not yet implemented")
    }

    private fun createClassEntryFromUmlClass(umlClass: Class): ClassTableEntry {
        val uuid = umlClass.eResource()?.getURIFragment(umlClass) ?: ""
        val name = umlClass.name ?: ""
        val visibility = umlClass.visibility.literal ?: ""
        val isAbstract = umlClass.isAbstract
        val isFinal = umlClass.isFinalSpecialization
        val superClassName = umlClass.superClasses.firstOrNull()?.name ?: ""
        val interfaces = umlClass.usedInterfaces.map { it.name } ?: emptyList()
        val attributeCount = umlClass.attributes.size
        val methodCount = umlClass.operations.size
        val linesOfCode = umlClass.ownedBehaviors.filterIsInstance<OpaqueBehavior>().sumBy { it.bodies.size } ?: 0
        return ClassTableEntry(uuid, name, visibility, isAbstract, isFinal, superClassName, interfaces, attributeCount, methodCount, linesOfCode)
    }

    /**
     * Maps the given view to all windows it can find within the view.
     * @param rootObjects The view to map.
     * @return The names of the windows that are available in the view.
     */
    override fun mapViewToWindows(rootObjects: List<EObject>): Set<String> {
        val windows = mutableSetOf<String>()
        for (rootObject in rootObjects) {
            val iterator = rootObject.eAllContents()
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (next is Package) {
                    windows.add(next.name)
                }
            }
            if(rootObject is Package){
                windows.add(rootObject.name)
            }
        }
        return windows
    }

    /**
     * Gets the display content of this view mapper, which is able to map the view content to a json string and vice versa.
     * @return The display content of this view mapper.
     */
    override fun getDisplayContent(): DisplayContentMapper<Table<ClassTableEntry>> {
        return TableDisplayContentMapper.create<ClassTableEntry>()
    }



}