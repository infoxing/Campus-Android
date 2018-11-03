package de.tum.`in`.tumcampusapp.core

import android.content.Context

interface FcmUploader {

    fun tryToUploadFcmToken(context: Context)

}
