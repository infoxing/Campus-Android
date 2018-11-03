package de.tum.`in`.tumcampusapp.locations

import android.content.Context
import android.location.Location
import de.tum.`in`.tumcampusapp.api.app.TUMCabeClient
import de.tum.`in`.tumcampusapp.core.Utils
import de.tum.`in`.tumcampusapp.database.TcaDb
import de.tum.`in`.tumcampusapp.database.daos.BuildingToGpsDao
import de.tum.`in`.tumcampusapp.model.locations.BuildingToGps
import de.tum.`in`.tumcampusapp.model.locations.Geo
import java.io.IOException

class TumLocationManager(private val context: Context) {

    private val buildingToGpsDao: BuildingToGpsDao by lazy {
        TcaDb.getInstance(context).buildingToGpsDao()
    }

    private val locationManager: LocationManager by lazy {
        LocationManager(context)
    }

    /**
     * Get Building ID accroding to the current location
     * Do not call on UI thread.
     *
     * @return the id of current building
     */
    fun getBuildingIDFromCurrentLocation(likelyNextLocation: Geo?): String? {
        val location = locationManager.getCurrentOrNextLocation(likelyNextLocation)
        return getBuildingIDFromLocation(location)
    }

    /**
     * Get Building ID based on the given location. Do not call on UI thread.
     *
     * @param location the give location
     * @return the id of current building
     */
    private fun getBuildingIDFromLocation(location: Location): String? {
        val buildingsWithGps = fetchBuildingsToGps()
        if (buildingsWithGps.isEmpty()) {
            return null
        }

        val distances = buildingsWithGps
                .map { Pair(it.latitude.toDouble(), it.longitude.toDouble()) }
                .map { getDistance(it.first, it.second, location.latitude, location.longitude) }

        val closestBuildingWithDistance = buildingsWithGps
                .zip(distances)
                .sortedBy { it.second /* the distance */ }
                .first()
                .takeIf { it.second < 1_000 }

        return closestBuildingWithDistance?.first?.id
    }

    private fun getDistance(lhsLat: kotlin.Double, lhsLng: kotlin.Double, rhsLat: kotlin.Double, rhsLng: kotlin.Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lhsLat, lhsLng, rhsLat, rhsLng, results)
        return results.first()
    }

    /**
     * This method tries to get the list of BuildingToGps by querying database or requesting
     * the server. If both ways fail, it returns an empty list.
     * @return The list of [BuildingToGps]
     */
    private fun fetchBuildingsToGps(): List<BuildingToGps> {
        val result = buildingToGpsDao.all.orEmpty()
        if (result.isNotEmpty()) {
            return result
        }

        return try {
            val newResult = TUMCabeClient.getInstance(context).building2Gps.orEmpty()
            buildingToGpsDao.insert(*newResult.toTypedArray())
            newResult
        } catch (e: IOException) {
            Utils.log(e)
            emptyList()
        }
    }

}
