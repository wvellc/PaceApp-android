package com.wvelabs.core_ui.image

import android.content.Context
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import okio.Path.Companion.toPath

object CoilImageLoaderFactory {

    fun create(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                // The essential "engine" for network calls
                add(OkHttpNetworkFetcherFactory())
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25) // Use 25% of available RAM
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache").absolutePath.toPath())
                    .maxSizeBytes(1024L * 1024 * 100) // 100MB Fixed Cache
                    .build()
            }
            .crossfade(true)
            .build()
    }
}