package de.tum.`in`.tumcampusapp.model.ticket.payload

import com.google.gson.annotations.SerializedName

data class EphimeralKey(@SerializedName("api_version")
                        var apiVersion: String = "")