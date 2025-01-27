package tools.vitruv.vitruvAdapter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class VitruvEditorAdapterApplication

fun main(args: Array<String>) {
    runApplication<VitruvEditorAdapterApplication>(*args)
}
