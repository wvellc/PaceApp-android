package com.wvelabs.core_network.model

/**
 * A sealed interface representing the outcome of an API call.
 * The `out T` (covariance) allows us to safely cast Success<Child> to Success<Parent> if needed.
 */
sealed interface NetworkResult<out T> {

    /**
     * Holds the successfully parsed data model.
     */
    data class Success<T>(val data: T) : NetworkResult<T>

    /**
     * Holds our agnostic NetworkError.
     * Notice it uses `Nothing` because a failure doesn't contain the requested data type.
     */
    data class Failure(val error: NetworkError) : NetworkResult<Nothing>
}