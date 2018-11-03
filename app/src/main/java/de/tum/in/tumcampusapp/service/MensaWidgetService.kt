package de.tum.`in`.tumcampusapp.service

import android.content.Intent
import android.widget.RemoteViewsService
import de.tum.`in`.tumcampusapp.ui.cafeteria.widget.MensaRemoteViewFactory

class MensaWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsService.RemoteViewsFactory {
        return MensaRemoteViewFactory(this.applicationContext)
    }

}

