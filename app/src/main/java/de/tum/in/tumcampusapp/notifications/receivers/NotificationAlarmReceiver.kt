package de.tum.`in`.tumcampusapp.notifications.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.tum.`in`.tumcampusapp.notifications.NotificationScheduler
import de.tum.`in`.tumcampusapp.model.activealarms.NotificationType
import de.tum.`in`.tumcampusapp.ui.tutionfees.TuitionFeesNotificationProvider
import de.tum.`in`.tumcampusapp.ui.cafeteria.CafeteriaNotificationProvider
import de.tum.`in`.tumcampusapp.ui.transportation.TransportNotificationProvider
import de.tum.`in`.tumcampusapp.core.Const
import org.jetbrains.anko.doAsync

class NotificationAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val typeId = intent.getLongExtra(Const.KEY_NOTIFICATION_TYPE_ID, 0)
        val type = NotificationType.fromId(typeId)

        val notificationProvider = when (type) {
            NotificationType.CAFETERIA -> CafeteriaNotificationProvider(context)
            NotificationType.TRANSPORT -> TransportNotificationProvider(context)
            NotificationType.TUITION_FEES -> TuitionFeesNotificationProvider(context)
            else -> return
        }

        doAsync {
            val notification = notificationProvider.buildNotification()
            notification?.let {
                NotificationScheduler(context).schedule(it)
            }
        }
    }

}
