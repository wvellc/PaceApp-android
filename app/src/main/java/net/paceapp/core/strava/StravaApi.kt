package net.paceapp.core.strava

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// Retrofit client for the app's OWN Firebase HTTPS functions (NOT Strava directly).
// Every call carries a Firebase ID token so the function knows the user; the function
// holds the Strava secret we never ship. Mirrors iOS StravaAPI. The functions are plain
// `onRequest` endpoints (POST JSON), so this is a normal REST call, not a callable.
interface StravaApi {

    @POST("stravaExchange")
    suspend fun exchange(
        @Header("Authorization") bearer: String,
        @Body body: StravaExchangeRequest,
    ): StravaExchangeResponse

    @POST("stravaBackfill")
    suspend fun backfill(
        @Header("Authorization") bearer: String,
        @Body body: StravaEmptyRequest = StravaEmptyRequest(),
    ): StravaBackfillResponse

    @POST("stravaDisconnect")
    suspend fun disconnect(
        @Header("Authorization") bearer: String,
        @Body body: StravaEmptyRequest = StravaEmptyRequest(),
    ): StravaDisconnectResponse
}

// Serializes to `{}` — the backfill/disconnect functions expect a JSON body (express.json()).
@Serializable
class StravaEmptyRequest

@Serializable
data class StravaExchangeRequest(val code: String)

@Serializable
data class StravaExchangeResponse(val athleteName: String? = null)

@Serializable
data class StravaBackfillResponse(val synced: Int = 0)

@Serializable
data class StravaDisconnectResponse(val disconnected: Boolean = false)

// Error envelope every function returns on non-2xx: { "error": "<friendly message>" }.
@Serializable
data class StravaErrorResponse(val error: String? = null)
