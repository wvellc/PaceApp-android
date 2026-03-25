package com.wvelabs.core_network.config

/**
 * The contract for any API endpoint in the system.
 * The actual endpoints are defined in the :app module.
 */
interface AppEndpoint {
    val path: String
    val method: String
    
    // Optional: Add headers or auth requirements specific to an endpoint
    val requiresAuth: Boolean 
        get() = true 
}