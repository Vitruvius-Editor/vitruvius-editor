package tools.vitruv.vitruvAdapter.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tools.vitruv.vitruvAdapter.dto.*
import tools.vitruv.vitruvAdapter.services.ConnectionService
import java.util.*

/**
 * This rest controller handles all requests that deal with the management of connections.
 *
 */
@CrossOrigin
@RestController
@RequestMapping(value = ["/api/v1"])
class ConnectionController {
    @Autowired
    private lateinit var connectionService: ConnectionService

    /**
     * This method returns a list of all saved connections.
     *
     * @return The list of all connections.
     */
    @GetMapping("/connections")
    fun getConnections(): ResponseEntity<Set<ConnectionResponse>> =
        ResponseEntity.ok(connectionService.getConnections().map { ConnectionResponse(it) }.toSet())

    /**
     * This method returns the data of a single connection.
     *
     * @param id The id of the connection.
     * @return The content of the connection.
     */
    @GetMapping("/connection/{id}")
    fun getConnection(
        @PathVariable("id") id: UUID,
    ): ResponseEntity<ConnectionResponse> = ResponseEntity.ok(ConnectionResponse(connectionService.getConnectionById(id)))

    /**
     * This method creates a new connection and returns its data.
     *
     * @param body Information required to create a new connection.
     * @return The content of the new connection.
     */
    @PostMapping("/connection")
    fun createConnection(
        @RequestBody body: ConnectionCreationRequest,
    ): ResponseEntity<ConnectionResponse> = ResponseEntity.ok(ConnectionResponse(connectionService.importConnection(body)))

    /**
     * This method deletes a connection.
     *
     * @param id The id of the connection to delete.
     * @return An empty return value containing the status code.
     */
    @DeleteMapping("/connection/{id}")
    fun deleteConnection(
        @PathVariable("id") id: UUID,
    ): ResponseEntity<Unit> = ResponseEntity.ok(connectionService.deleteConnection(id))

    /**
     * This method edits a connection.
     *
     * @param id The id of the connection to edit
     * @param body The data to edit.
     * @return The new content of the connection.
     */
    @PutMapping("/connection/{id}")
    fun editConnection(
        @PathVariable("id") id: UUID,
        @RequestBody body: ConnectionEditRequest,
    ): ResponseEntity<ConnectionResponse> = ResponseEntity.ok(ConnectionResponse(connectionService.editConnection(id, body)))
}
