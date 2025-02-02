package vitruv.tools.vitruvadpter.testServer

import org.apache.commons.jxpath.Container
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import org.eclipse.uml2.uml.UMLFactory
import tools.mdsd.jamopp.model.java.JavaPackage
import tools.mdsd.jamopp.model.java.classifiers.Class
import tools.mdsd.jamopp.model.java.containers.ContainersFactory
import tools.mdsd.jamopp.model.java.containers.JavaRoot
import tools.mdsd.jamopp.model.java.containers.impl.PackageImpl
import tools.mdsd.jamopp.model.java.impl.JavaFactoryImpl
import tools.mdsd.jamopp.model.java.impl.JavaPackageImpl
import tools.mdsd.jamopp.parser.jdt.singlefile.JaMoPPJDTSingleFileParser
import tools.mdsd.jamopp.printer.JaMoPPPrinter
import tools.mdsd.jamopp.resource.JavaResource2Factory
import tools.vitruv.framework.remote.client.VitruvClientFactory
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Path


/**
 * Starts the server
 */

fun main() {
    val serverInitializer = ServerInitializer()
    val server = serverInitializer.initialize()
    server.start()
    println("Vitruvius server started on: " + serverInitializer.serverPort)



    //testJavaView(serverInitializer)



    //clientTest(serverInitializer)
}

fun testJavaView(serverInitializer: ServerInitializer){

    val client = VitruvClientFactory.create("localhost", serverInitializer.serverPort, Path.of("vitruv_server/test"))
    var clientViewTypes = client.viewTypes

    var umlViewType = clientViewTypes.stream().filter { it.name == "JAVA" }.findAny()

    var umlSelector = umlViewType.get().createSelector(null)
    umlSelector.selectableElements.forEach { it -> umlSelector.setSelected(it, true) }
    var umlView = umlSelector.createView().withChangeDerivingTrait()


    val rootObeejts = umlView.rootObjects
    print(rootObeejts)

//    val classjava = umlView.rootObjects.first() as Class
//
//    val packageImpl = ContainersFactory.eINSTANCE.createPackage()
//    packageImpl.name = "testPackage"
//    packageImpl.classifiers.add(classjava)
//
//    val javaRoot = packageImpl as JavaRoot
//
//    JaMoPPPrinter.print(javaRoot, Path.of("C:\\Users\\amira\\Desktop\\vitruvius-editor2\\vitruv_server\\src\\main\\resources\\output.txt"))
//



}


/**
 * Test the client
 */
fun clientTest(serverInitializer: ServerInitializer) {
    val client = VitruvClientFactory.create("localhost", serverInitializer.serverPort, Path.of("vitruv_server/test"))
    var clientViewTypes = client.viewTypes

    var umlViewType = clientViewTypes.stream().filter { it.name == "UML" }.findAny()

    print(umlViewType.get().name)

    var umlSelector = umlViewType.get().createSelector(null)
    umlSelector.selectableElements.forEach { it -> umlSelector.setSelected(it, true) }

    var umlView = umlSelector.createView().withChangeDerivingTrait()
    val umlPackage = umlView.rootObjects.first() as org.eclipse.uml2.uml.Package


    var factory = UMLFactory.eINSTANCE
    var newClass = factory.createClass()
    newClass.name = "NewClass"
    umlPackage.packagedElements.add(newClass)
    umlView.commitChangesAndUpdate()

    print(umlPackage.packagedElements)
    val umlPackage1 = umlView.rootObjects.first() as org.eclipse.uml2.uml.Package
    val umlClass = umlPackage1.packagedElements.first() as org.eclipse.uml2.uml.Class
    val att = umlClass.createOwnedAttribute("newAttribute", null)
    att.setIsStatic(true)
    umlView.commitChangesAndUpdate()
    umlView.close()
}