package tools.vitruv.vitruvAdapter.exception


/**
 * Exception that is thrown when the connection to the Vitruvius server failed.
 *
 * @param errorMsg The error message.
 */
class VitruviusConnectFailedException(
    val errorMsg: String,
) : Exception() {
    override val message = "Connection to Vitruvius server failed. $errorMsg"
}
