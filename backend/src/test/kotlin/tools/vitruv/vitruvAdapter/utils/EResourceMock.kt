package tools.vitruv.vitruvAdapter.utils

import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceImpl
import org.mockito.Mockito

/**
 * This utility class is used to mock an EResource for a list of EObjects.
 */
class EResourceMock private constructor() {

    companion object {

        /**
         * This method is used to mock an EResource for a list of EObjects.
         * This is needed to get the UUID Fragment of an EObject, by the EResource, which may not exist for an EObject.
         * The mocked EResource will return the getFakeUUID(eObject) for the getUUIDFragment(eObject) call.
         * @param eObjects The list of EObjects for which the EResource should be mocked.
         * @return The list of EObjects with mocked EResource.
         */
        fun mockERessourceForEObjects(eObjects: List<EObject>): List<EObject>{
            val mockedEObjects = mutableListOf<EObject>()
            val resource = ResourceImpl()
            //val mockedRessource: Resource = Mockito.mock(Resource::class.java)
            val mockedRessource: Resource = Mockito.spy(resource)
            for (eObject in eObjects) {
                val eObjectSpy = Mockito.spy(eObject)
                Mockito.doReturn(mockedRessource).`when`(eObjectSpy).eResource()
                val uuid = getFakeUUID(eObjectSpy)
                Mockito.`when`(mockedRessource.getURIFragment(eObjectSpy)).thenReturn(uuid)
                mockedEObjects.add(eObjectSpy)
            }
            return mockedEObjects
        }

        /**
         * This method is used to mock an EResource for an EObject.
         * This is needed to get the UUID Fragment of an EObject, by the EResource, which may not exist for an EObject.
         * The mocked EResource will return the getFakeUUID(eObject) for the getUUIDFragment(eObject) call.
         * @param eObject The EObject for which the EResource should be mocked.
         * @return The EObject with mocked EResource.
         */
        fun <T:EObject> mockERessourceForEObject(eObject: T): T {
            val resource = ResourceImpl()
            //val mockedRessource: Resource = Mockito.mock(Resource::class.java)
            val mockedRessource: Resource = Mockito.spy(resource)
            val eObjectSpy = Mockito.spy(eObject)
            Mockito.doReturn(mockedRessource).`when`(eObjectSpy).eResource()
            val uuid = getFakeUUID(eObjectSpy)
            Mockito.`when`(mockedRessource.getURIFragment(eObjectSpy)).thenReturn(uuid)
            return eObjectSpy
        }


        /**
         * This method is used to get a fake UUID for an EObject.
         * Should be used to mock the UUID of an EObject and be unique for each EObject.
         * @param eObject The EObject for which the UUID should be generated.
         * @return The fake UUID for the EObject.
         */
        fun getFakeUUID(eObject: EObject): String {
            return eObject.toString().hashCode().toString()
        }
    }
}