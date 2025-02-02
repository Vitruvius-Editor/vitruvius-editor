package tools.vitruv.vitruvAdapter.exception

/**
 * Exception that is thrown when a view cannot be displayed.
 */

class DisplayViewException(message: String) : Exception(message) {
    constructor(message: String, cause: Throwable) : this(message) {
        initCause(cause)
    }
}