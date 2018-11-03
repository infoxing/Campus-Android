package de.tum.`in`.tumcampusapp.model.transportation

import com.google.gson.Gson
import com.google.gson.JsonObject
import de.tum.`in`.tumcampusapp.model.recents.Recent

data class StationResult(
        val station: String = "",
        val id: String = "",
        val quality: Int = 0
) {

    override fun toString(): String = station

    companion object {
        fun fromRecent(r: Recent): StationResult? {
            return Gson().fromJson(r.name, StationResult::class.java)
        }

        fun fromJson(json: JsonObject): StationResult {
            return StationResult(
                    json.get("name").asString,
                    json.getAsJsonObject("ref").get("id").asString,
                    json.get("quality")?.asInt ?: 0
            )
        }
    }
}
