package de.tum.in.tumcampusapp.fcm;

import android.app.Notification;
import android.content.Context;

import java.io.IOException;

import androidx.annotation.Nullable;
import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.api.app.TUMCabeClient;
import de.tum.in.tumcampusapp.core.Utils;

public abstract class GenericNotification {

    protected final int type;
    protected final int notification;
    protected final int icon = R.drawable.ic_notification;
    protected final boolean confirmation;

    protected final Context context;

    public GenericNotification(Context context, int type, int notification, boolean confirmation) {
        this.notification = notification;
        this.context = context;
        this.confirmation = confirmation;
        this.type = type;
    }

    public void sendConfirmation() throws IOException {
        //Legacy support: notification id is -1 when old gcm messages arrive
        if (!this.confirmation || this.notification == -1) {
            return;
        }
        Utils.logv("Confirmed notification " + this.notification);
        TUMCabeClient.getInstance(this.context)
                     .confirm(this.notification);
    }

    @Nullable
    public abstract Notification getNotification();

    public abstract int getNotificationIdentification();

}
