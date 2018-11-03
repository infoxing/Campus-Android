package de.tum.`in`.tumcampusapp.utils

import android.content.Context
import de.tum.`in`.tumcampusapp.api.shared.CacheControl
import de.tum.`in`.tumcampusapp.api.tumonline.TUMOnlineClient
import de.tum.`in`.tumcampusapp.component.tumui.calendar.CalendarController
import de.tum.`in`.tumcampusapp.component.ui.chat.ChatRoomController
import de.tum.`in`.tumcampusapp.core.Utils
import de.tum.`in`.tumcampusapp.model.calendar.EventsResponse
import de.tum.`in`.tumcampusapp.model.lecture.LecturesResponse
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BackgroundUpdater(private val context: Context) {

    fun update() {
        doAsync {
            syncCalendar()
            syncPersonalLectures()
        }
    }

    private fun syncCalendar() {
        TUMOnlineClient
                .getInstance(context)
                .getCalendar(CacheControl.USE_CACHE)
                .enqueue(object : Callback<EventsResponse> {
                    override fun onResponse(call: Call<EventsResponse>, response: Response<EventsResponse>) {
                        val eventsResponse = response.body() ?: return
                        val events = eventsResponse.events ?: return
                        CalendarController(context).importCalendar(events)
                        loadRoomLocations()
                    }

                    override fun onFailure(call: Call<EventsResponse>, t: Throwable) {
                        Utils.log(t, "Error while loading calendar in CacheManager")
                    }
                })
    }

    private fun loadRoomLocations() {
        doAsync {
            CalendarController.QueryLocationsService.loadGeo(context)
        }
    }

    private fun syncPersonalLectures() {
        TUMOnlineClient
                .getInstance(context)
                .getPersonalLectures(CacheControl.USE_CACHE)
                .enqueue(object : Callback<LecturesResponse> {
                    override fun onResponse(call: Call<LecturesResponse>,
                                            response: Response<LecturesResponse>) {
                        Utils.log("Successfully updated personal lectures in background")
                        val lectures = response.body()?.lectures ?: return
                        val chatRoomController = ChatRoomController(context)
                        chatRoomController.createLectureRooms(lectures)
                    }

                    override fun onFailure(call: Call<LecturesResponse>, t: Throwable) {
                        Utils.log(t, "Error loading personal lectures in background")
                    }
                })
    }

}
