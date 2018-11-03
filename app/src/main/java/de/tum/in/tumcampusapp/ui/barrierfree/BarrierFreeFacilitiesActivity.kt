package de.tum.`in`.tumcampusapp.ui.barrierfree

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.database.TcaDb
import de.tum.`in`.tumcampusapp.database.daos.RecentsDao
import de.tum.`in`.tumcampusapp.locations.TumLocationManager
import de.tum.`in`.tumcampusapp.model.recents.Recent
import de.tum.`in`.tumcampusapp.model.roomfinder.RoomFinderRoom
import de.tum.`in`.tumcampusapp.ui.calendar.CalendarController
import de.tum.`in`.tumcampusapp.ui.generic.activity.ActivityForAccessingTumCabe
import de.tum.`in`.tumcampusapp.ui.roomfinder.RoomFinderDetailsActivity
import de.tum.`in`.tumcampusapp.ui.roomfinder.RoomFinderListAdapter
import de.tum.`in`.tumcampusapp.ui.roomfinder.viewmodel.RoomFinderRoomViewEntity
import kotlinx.android.synthetic.main.activity_barrier_free_facilities.*
import retrofit2.Call

class BarrierFreeFacilitiesActivity : ActivityForAccessingTumCabe<List<RoomFinderRoom>>(
        R.layout.activity_barrier_free_facilities
), AdapterView.OnItemSelectedListener {

    private val recents: RecentsDao by lazy {
        TcaDb.getInstance(this).recentsDao()
    }

    private val locationManager: TumLocationManager by lazy {
        TumLocationManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        spinnerToolbar.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val apiCall = when (position) {
            0 -> getApiCallForCurrentLocation()
            1 -> apiClient.listOfToilets
            else -> apiClient.listOfElevators
        }

        apiCall?.let { fetch(it) } ?: showError(R.string.error_something_wrong)
    }

    private fun getApiCallForCurrentLocation(): Call<List<RoomFinderRoom>>? {
        val calendarController = CalendarController(this)
        val likelyNextLocation = calendarController.nextCalendarItemGeo
        val buildingId = locationManager.getBuildingIDFromCurrentLocation(likelyNextLocation) ?: return null
        return apiClient.getListOfNearbyFacilities(buildingId)
    }

    override fun onDownloadSuccessful(response: List<RoomFinderRoom>) {
        val rooms = response.map { RoomFinderRoomViewEntity.create(it) }
        barrierFreeFacilitiesListView.adapter = RoomFinderListAdapter(this, rooms)
        barrierFreeFacilitiesListView.setOnItemClickListener { _, _, index, _ ->
            val facility = response[index]
            recents.insert(Recent(facility.toString(), RecentsDao.ROOMS))
            openRoomFinderDetails(facility)
        }
    }

    private fun openRoomFinderDetails(facility: RoomFinderRoom) {
        val intent = Intent(this, RoomFinderDetailsActivity::class.java)
        intent.putExtra(RoomFinderDetailsActivity.EXTRA_ROOM_INFO, facility)
        startActivity(intent)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Nothing selected
    }

}
