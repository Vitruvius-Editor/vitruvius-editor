package tools.vitruv.vitruvAdapter.core.api

import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import org.eclipse.uml2.uml.UMLPackage
import org.eclipse.uml2.uml.internal.impl.UMLPackageImpl
import org.springframework.stereotype.Service
import tools.mdsd.jamopp.model.java.JavaPackage
import tools.mdsd.jamopp.model.java.impl.JavaPackageImpl
import tools.vitruv.change.atomic.AtomicPackage
import tools.vitruv.change.atomic.impl.AtomicPackageImpl
import tools.vitruv.change.correspondence.CorrespondencePackage
import tools.vitruv.change.correspondence.impl.CorrespondencePackageImpl
import tools.vitruv.framework.views.View
import tools.vitruv.framework.remote.client.VitruvClient
import tools.vitruv.framework.remote.client.exception.BadClientResponseException
import tools.vitruv.framework.remote.client.exception.BadServerResponseException
import tools.vitruv.framework.views.ViewSelector
import tools.vitruv.framework.views.ViewType
import tools.vitruv.vitruvAdapter.exception.VitruviusConnectFailedException
import tools.vitruv.vitruvAdapter.exception.DisplayViewException

/**
 * This class is the adapter for the Vitruvius model server. It provides methods to interact with the model server.
 * This class contains the methods for the frontend to interact with the model server.
 */
@Service
class VitruvAdapter {
    init {
        Resource.Factory.Registry.INSTANCE.extensionToFactoryMap.put("*", XMIResourceFactoryImpl())
        EPackage.Registry.INSTANCE.put(JavaPackage.eNS_URI, JavaPackageImpl.eINSTANCE)
        EPackage.Registry.INSTANCE.put(CorrespondencePackage.eNS_URI, CorrespondencePackageImpl.eINSTANCE)
        EPackage.Registry.INSTANCE.put(UMLPackage.eNS_URI, UMLPackageImpl.eINSTANCE)
        EPackage.Registry.INSTANCE.put(AtomicPackage.eNS_URI, AtomicPackageImpl.eINSTANCE)
    }
    private var vitruvClient: VitruvClient? = null
    private var displayViewContainer: DisplayViewContainer? = null


    /**
     * Connects the adapter to the model server.
     * @param vitruvClient The client to connect to the model server.
     */
    fun connectClient(vitruvClient: VitruvClient) {
        try {
            vitruvClient.viewTypes
        } catch (_: BadClientResponseException) {
            throw VitruviusConnectFailedException("Could not connect to model server.")
        } catch (_: BadServerResponseException) {
            throw VitruviusConnectFailedException("Could not connect to model server.")
        }

        this.vitruvClient = vitruvClient
    }

    /**
     * Sets the DisplayViewContainer for the adapter.
     * @param displayViewContainer The DisplayViewContainer to set.
     */
    fun setDisplayViewContainer(displayViewContainer: DisplayViewContainer) {
        this.displayViewContainer = displayViewContainer
    }


    /**
     * Returns all available DisplayViews.
     * @return The available DisplayViews.
     */
    fun getDisplayViews(): Set<DisplayView> = displayViewContainer?.getDisplayViews() ?: emptySet()

    /**
     * Returns a DisplayView by its name.
     * @param name The name of the DisplayView.
     * @return The DisplayView with the given name or null if no DisplayView with the given name is registered.
     */
    fun getDisplayView(name: String): DisplayView? = displayViewContainer?.getDisplayView(name)

    /**
     * Gets all windows that are available for a given DisplayView.
     * @param displayView The DisplayView to get the windows for.
     * @return The windows that are available for the given DisplayView.
     */
    fun getWindows(displayView : DisplayView): Set<String> {
        val view = getView(displayView)
        return displayView.viewMapper.mapViewToWindows(view.rootObjects.toList())
    }

    /**
     * Creates a view for the given DisplayView with its internal selector.
     * @param displayView The DisplayView to create the view for.
     * @return The created view.
     */
    private fun getView(displayView: DisplayView): View {
        val internalSelector = getViewType(displayView).createSelector(null)
        displayView.internalSelector.applySelection(internalSelector)
        return internalSelector.createView()
    }

    /**
     * Creates the content that can be displayed in the Vitruvius graphical editor for the given windows.
     * @param windows The windows to create the content for.
     * @return The created content for the windows.
     */
    fun createWindowContent(displayView: DisplayView, windows: Set<String>): String {
        val view = getView(displayView)
        val selectedEObjects = displayView.contentSelector.applySelection(view.rootObjects.toList(), windows)
        val mapper = displayView.viewMapper
        val viewInformation = JsonViewInformation(mapper.getDisplayContent())
        val mappedData = mapper.mapEObjectsToWindowsContent(selectedEObjects)
        val json = viewInformation.parseWindowsToJson(mappedData)
        return json
    }


    /**
     * This method reverts the json that Theia can interpret to display views to EObjects and tries to
     * apply the changes to the model by state changed derivation strategy.
     * @param displayView The DisplayView to edit.
     * @param json The json to edit the DisplayView with.
     */
    fun editDisplayView(displayView: DisplayView, json: String) {
        val mapper = displayView.viewMapper
        val viewInformation = JsonViewInformation(mapper.getDisplayContent())
        val oldView = getView(displayView).withChangeDerivingTrait()
        val oldSelectedEObjects = displayView.contentSelector.applySelection(oldView.rootObjects.toList(), viewInformation.collectWindowsFromJson(json))
        mapper.mapWindowsToEObjectsAndApplyChangesToEObjects(oldSelectedEObjects, viewInformation.parseWindowsFromJson(json))
        oldView.commitChanges()
    }

    /**
     * Collects the windows from the given json string.
     * @param displayView The DisplayView to collect the windows for.
     * @param json The json string to collect the windows from.
     * @return The collected windows.
     */
    fun collectWindowsFromJson(displayView: DisplayView, json: String): Set<String> {
        val mapper = displayView.viewMapper
        val viewInformation = JsonViewInformation(mapper.getDisplayContent())
        return viewInformation.collectWindowsFromJson(json)
    }


    private fun getViewType(displayView: DisplayView): ViewType<out ViewSelector> {
        val client = vitruvClient ?: throw IllegalStateException("No client connected.")
        val viewType = client.viewTypes.stream().filter{it.name == displayView.viewTypeName}.findAny()
        if (viewType.isEmpty) {
            throw DisplayViewException("ViewType ${displayView.viewTypeName} not found.")
        }
        return viewType.get()
    }
}
