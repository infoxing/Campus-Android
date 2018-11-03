package de.tum.`in`.tumcampusapp.service

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import de.tum.`in`.tumcampusapp.core.Const
import de.tum.`in`.tumcampusapp.core.Utils
import de.tum.`in`.tumcampusapp.utils.BackgroundUpdater

/**
 * Service used to fill caches in background, for faster/offline access
 */
class FillCacheService : JobIntentService() {

    override fun onCreate() {
        super.onCreate()
        Utils.logv("FillCacheService has started")
    }

    override fun onDestroy() {
        super.onDestroy()
        Utils.logv("FillCacheService has stopped")
    }

    override fun onHandleWork(intent: Intent) {
        val backgroundUpdater = BackgroundUpdater(this)
        backgroundUpdater.update()
    }

    companion object {

        @JvmStatic fun enqueueWork(context: Context, work: Intent) {
            JobIntentService.enqueueWork(context,
                    FillCacheService::class.java, Const.FILL_CACHE_SERVICE_JOB_ID, work)
        }

    }

}