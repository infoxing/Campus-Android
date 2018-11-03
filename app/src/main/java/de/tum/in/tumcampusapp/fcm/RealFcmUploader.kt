package de.tum.`in`.tumcampusapp.fcm

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import de.tum.`in`.tumcampusapp.core.Const
import de.tum.`in`.tumcampusapp.core.FcmUploader
import de.tum.`in`.tumcampusapp.core.Utils
import de.tum.`in`.tumcampusapp.service.FcmTokenHandler

object RealFcmUploader : FcmUploader {

    override fun tryToUploadFcmToken(context: Context) {
        // Check device for Play Services APK. If check succeeds, proceed with FCM registration.
        // Can only be done after the public key has been uploaded
        if (Utils.getSettingBool(context, Const.PUBLIC_KEY_UPLOADED, false) && GoogleApiAvailability.getInstance()
                        .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
            FcmTokenHandler.checkSetup(context)
        }
    }

}
