package de.tum.`in`.tumcampusapp.component.ui.transportation.viewmodel

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.google.gson.JsonObject
import de.tum.`in`.tumcampusapp.model.recents.Recent
import de.tum.`in`.tumcampusapp.component.ui.transportation.TransportationDetailsActivity

data class StationResultViewEntity(
        val station: String = "",
        val id: String = "",
        val quality: Int = 0
) {

    override fun toString(): String = station

    fun getIntent(context: Context): Intent {
        return Intent(context, TransportationDetailsActivity::class.java).apply {
            putExtra(TransportationDetailsActivity.EXTRA_STATION_ID, id)
            putExtra(TransportationDetailsActivity.EXTRA_STATION, station)
        }
    }

    companion object {
        fun fromRecent(r: Recent): StationResultViewEntity? {
            return Gson().fromJson(r.name, StationResultViewEntity::class.java)
        }

        fun fromJson(json: JsonObject): StationResultViewEntity {
            return StationResultViewEntity(
                    json.get("name").asString,
                    json.getAsJsonObject("ref").get("id").asString,
                    json.get("quality")?.asInt ?: 0
            )
        }
    }
}