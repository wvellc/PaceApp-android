package com.example.paceapp

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.wvelabs.core_ui.image.CoilImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PaceApplication : Application(), SingletonImageLoader.Factory {
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return CoilImageLoaderFactory.create(context)
    }

}