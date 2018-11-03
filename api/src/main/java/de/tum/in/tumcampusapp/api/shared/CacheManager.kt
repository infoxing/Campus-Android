package de.tum.`in`.tumcampusapp.api.shared

import android.content.Context
import okhttp3.Cache

class CacheManager(private val context: Context) {

    val cache: Cache
        get() = Cache(context.cacheDir, 10 * 1024 * 1024) // 10 MB

    @Synchronized
    fun clearCache() {
        cache.delete()
    }

}