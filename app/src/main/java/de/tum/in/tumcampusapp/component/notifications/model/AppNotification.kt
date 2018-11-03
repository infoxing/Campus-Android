package de.tum.`in`.tumcampusapp.component.notifications.model

import android.app.Notification
import de.tum.`in`.tumcampusapp.model.activealarms.NotificationType
import de.tum.`in`.tumcampusapp.model.activealarms.ScheduledNotification

/**
 * Holds a [Notification] that is to be displayed to the user. Its subclasses can be used to
 * immediately display notifications via [InstantNotification] or to schedule notifications for
 * later via [FutureNotification].
 *
 * @param id The identifier of the notification
 * @param notification The [Notification] that will be displayed to the user
 */
abstract class AppNotification(
        val type: NotificationType,
        val id: Int,
        val notification: Notification
) {

    fun toScheduledNotification(): ScheduledNotification {
        return ScheduledNotification(type.id.toLong(), id)
    }

}