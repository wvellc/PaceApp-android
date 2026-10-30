package net.paceapp

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
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
        configureRevenueCat()
        remoteConfigManager.refresh()
    }

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
