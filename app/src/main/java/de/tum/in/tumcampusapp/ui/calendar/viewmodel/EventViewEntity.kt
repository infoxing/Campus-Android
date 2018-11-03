package de.tum.`in`.tumcampusapp.ui.calendar.viewmodel

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.notifications.model.FutureNotification
import de.tum.`in`.tumcampusapp.model.activealarms.NotificationType
import de.tum.`in`.tumcampusapp.core.Const
import de.tum.`in`.tumcampusapp.model.calendar.Event
import de.tum.`in`.tumcampusapp.model.locations.Geo
import de.tum.`in`.tumcampusapp.core.DateTimeUtils
import org.joda.time.DateTime

data class EventViewEntity(
        val description: String?,
        val startTime: DateTime?,
        val endTime: DateTime?,
        val geo: Geo?,
        val location: String?,
        val id: String?,
        val status: String?,
        val title: String,
        val url: String?,
        val futureNotification: FutureNotification?
) {

    val isFutureEvent: Boolean
        get() = startTime?.isAfterNow ?: false

    companion object {

        @JvmStatic
        fun create(context: Context, event: Event): EventViewEntity {
            val futureNotification = createFutureNotification(context, event)
            return EventViewEntity(
                    event.description, event.startTime, event.endTime, event.geo, event.location,
                    event.id, event.status, event.title, event.url, futureNotification
            )
        }

        private fun createFutureNotification(context: Context, event: Event): FutureNotification? {
            val id = event.id ?: return null
            val startTime = event.startTime ?: return null
            val endTime = event.endTime ?: return null

            val timestamp = DateTimeUtils.formatFutureTime(startTime, context)
            val duration = endTime.millis - startTime.millis

            val notification = NotificationCompat.Builder(context, Const.NOTIFICATION_CHANNEL_DEFAULT)
                    .setContentTitle(event.title)
                    .setContentText(timestamp)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_outline_event_24px)
                    .setShowWhen(false)
                    .setColor(ContextCompat.getColor(context, R.color.color_primary))
                    .setTimeoutAfter(duration)
                    .build()

            val notificationTime = startTime.minusMinutes(15)
            return FutureNotification(NotificationType.CALENDAR, id.toInt(), notification, notificationTime)
        }

    }

}
