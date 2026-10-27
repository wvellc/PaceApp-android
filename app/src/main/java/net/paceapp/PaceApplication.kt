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
import net.paceapp.core.revenuecat.RevenueCatConst

@HiltAndroidApp
class PaceApplication : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
        configureRevenueCat()
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
