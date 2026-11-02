package net.paceapp

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.wvelabs.core_ui.image.CoilImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import net.paceapp.core.config.RemoteConfigManager
import net.paceapp.core.revenuecat.RevenueCatConst
import javax.inject.Inject

@HiltAndroidApp
class PaceApplication : Application(), SingletonImageLoader.Factory {

    // Injected so Remote Config is constructed and its first fetch kicks off at launch.
    @Inject
    lateinit var remoteConfigManager: RemoteConfigManager

    override fun onCreate() {
        super.onCreate()
        configureAppCheck()
        configureRevenueCat()
        remoteConfigManager.refresh()
    }

    // App Check — install before any Firebase service call so requests carry an attestation token.
    // Release uses Play Integrity; debug uses the Debug provider (register the token logged at launch
    // in the Firebase console). The debug provider is a debugImplementation dep, so it's loaded
    // reflectively here to keep the attestation bypass out of the release build.
    private fun configureAppCheck() {
        FirebaseApp.initializeApp(this)
        val factory: AppCheckProviderFactory =
            if (BuildConfig.DEBUG) debugAppCheckFactory() else PlayIntegrityAppCheckProviderFactory.getInstance()
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(factory)
    }

    private fun debugAppCheckFactory(): AppCheckProviderFactory =
        Class.forName("com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory")
            .getMethod("getInstance")
            .invoke(null) as AppCheckProviderFactory

    // Configure RevenueCat once, as early as possible. Anonymous at launch; AuthManager
    // logs in the Firebase uid as soon as auth state resolves (parity with iOS).
    private fun configureRevenueCat() {
        if (BuildConfig.DEBUG) Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(this, RevenueCatConst.apiKey).build()
        )
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return CoilImageLoaderFactory.create(context)
    }

}
