package de.tum.`in`.tumcampusapp.service

import android.content.Context
import android.content.Intent
import de.tum.`in`.tumcampusapp.ui.calendar.CalendarController

object ServiceUtils {

    @JvmStatic
    fun resetAll(context: Context) {
        // Stop all services, since they might have instantiated Managers and cause SQLExceptions
        val services = arrayOf<Class<*>>(
                CalendarController.QueryLocationsService::class.java,
                SendMessageService::class.java,
                SilenceService::class.java,
                DownloadService::class.java,
                BackgroundService::class.java)

        for (service in services) {
            context.stopService(Intent(context, service))
        }
    }

}
