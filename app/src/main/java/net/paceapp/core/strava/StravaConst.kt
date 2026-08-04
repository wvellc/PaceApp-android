package net.paceapp.core.strava

import net.paceapp.BuildConfig

// Strava OAuth + Cloud Functions constants. Mirror of iOS `Utility/Constant/Strava.swift`
// — both platforms must use the same client id, redirect, scope and functions base URL
// so a single Strava API app + one shared Cloud Functions deployment serve both phones.
object StravaConst {

    // Strava "Client ID" from the Strava API dashboard, injected via secrets-gradle-plugin
    // (secrets.properties / local.defaults.properties → BuildConfig). Empty until the
    // owner fills it — the feature is dormant (connect surfaces a config error) until then.
    val clientId: String = BuildConfig.STRAVA_CLIENT_ID

    // The custom scheme the app's deep-link handler listens for (StravaManager.handleCallback
    // + MainActivity + the AndroidManifest intent-filter validate scheme/host against these).
    const val CALLBACK_SCHEME = "paceapp"
    const val CALLBACK_HOST = "strava-callback"

    // The redirect_uri sent to Strava at authorize. Strava rejects/mishandles custom schemes,
    // so we use an https page on Firebase Hosting (Strava "Authorization Callback Domain" =
    // thepaceapp.web.app). That page relays the query back to "$CALLBACK_SCHEME://$CALLBACK_HOST",
    // which re-enters the app via the manifest intent-filter — so handleCallback is unchanged.
    const val REDIRECT_URI = "https://thepaceapp.web.app/stravaCallback"

    // activity:write → uploads, read → read back. Must match iOS.
    const val SCOPE = "activity:write,read"

    // Native Strava-app handoff vs web (Custom Tab) fallback.
    const val APP_AUTHORIZE_URL = "strava://oauth/mobile/authorize"
    const val WEB_AUTHORIZE_URL = "https://www.strava.com/oauth/mobile/authorize"

    // Shared Firebase HTTPS functions in the thepaceapp project (region us-central1).
    const val FUNCTIONS_BASE_URL = "https://us-central1-thepaceapp.cloudfunctions.net/"
}
