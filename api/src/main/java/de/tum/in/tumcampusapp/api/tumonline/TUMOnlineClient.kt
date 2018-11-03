package de.tum.`in`.tumcampusapp.api.tumonline

import android.content.Context
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import de.tum.`in`.tumcampusapp.api.shared.ApiHelper
import de.tum.`in`.tumcampusapp.api.shared.CacheControl
import de.tum.`in`.tumcampusapp.api.shared.CacheManager
import de.tum.`in`.tumcampusapp.api.shared.interceptors.AddTokenInterceptor
import de.tum.`in`.tumcampusapp.api.shared.interceptors.CacheResponseInterceptor
import de.tum.`in`.tumcampusapp.api.shared.interceptors.CheckErrorInterceptor
import de.tum.`in`.tumcampusapp.api.shared.interceptors.CheckTokenInterceptor
import de.tum.`in`.tumcampusapp.api.tumonline.model.AccessToken
import de.tum.`in`.tumcampusapp.api.tumonline.model.TokenConfirmation
import de.tum.`in`.tumcampusapp.core.Const
import de.tum.`in`.tumcampusapp.model.calendar.CreateEventResponse
import de.tum.`in`.tumcampusapp.model.calendar.DeleteEventResponse
import de.tum.`in`.tumcampusapp.model.calendar.EventsResponse
import de.tum.`in`.tumcampusapp.model.grades.ExamList
import de.tum.`in`.tumcampusapp.model.lecture.LectureAppointmentsResponse
import de.tum.`in`.tumcampusapp.model.lecture.LectureDetailsResponse
import de.tum.`in`.tumcampusapp.model.lecture.LecturesResponse
import de.tum.`in`.tumcampusapp.model.person.Employee
import de.tum.`in`.tumcampusapp.model.person.IdentitySet
import de.tum.`in`.tumcampusapp.model.person.PersonList
import de.tum.`in`.tumcampusapp.model.tuition.TuitionList
import retrofit2.Call
import retrofit2.Retrofit

class TUMOnlineClient(private val apiService: TUMOnlineAPIService) {

    fun getCalendar(cacheControl: CacheControl): Call<EventsResponse> {
        return apiService.getCalendar(
                Const.CALENDAR_MONTHS_BEFORE, Const.CALENDAR_MONTHS_AFTER, cacheControl.header)
    }

    fun createEvent(title: String, description: String,
                    start: String, end: String, eventId: String?): Call<CreateEventResponse> {
        return apiService.createCalendarEvent(title, description, start, end, eventId)
    }

    fun deleteEvent(eventId: String): Call<DeleteEventResponse> {
        return apiService.deleteCalendarEvent(eventId)
    }

    fun getTuitionFeesStatus(cacheControl: CacheControl): Call<TuitionList> {
        return apiService.getTuitionFeesStatus(cacheControl.header)
    }

    fun getPersonalLectures(cacheControl: CacheControl): Call<LecturesResponse> {
        return apiService.getPersonalLectures(cacheControl.header)
    }

    fun getLectureDetails(id: String, cacheControl: CacheControl): Call<LectureDetailsResponse> {
        return apiService.getLectureDetails(id, cacheControl.header)
    }

    fun getLectureAppointments(id: String, cacheControl: CacheControl): Call<LectureAppointmentsResponse> {
        return apiService.getLectureAppointments(id, cacheControl.header)
    }

    fun searchLectures(query: String): Call<LecturesResponse> {
        return apiService.searchLectures(query)
    }

    fun getPersonDetails(id: String, cacheControl: CacheControl): Call<Employee> {
        return apiService.getPersonDetails(id, cacheControl.header)
    }

    fun searchPerson(query: String): Call<PersonList> {
        return apiService.searchPerson(query)
    }

    fun getGrades(cacheControl: CacheControl): Call<ExamList> {
        return apiService.getGrades(cacheControl.header)
    }

    fun requestToken(username: String, tokenName: String): Call<AccessToken> {
        return apiService.requestToken(username, tokenName)
    }

    fun getIdentity(): Call<IdentitySet> = apiService.getIdentity()

    fun uploadSecret(token: String, secret: String): Call<TokenConfirmation> {
        return apiService.uploadSecret(token, secret)
    }

    companion object {

        private const val BASE_URL = "https://campus.tum.de/tumonline/"
        // For testing
        // private const val BASE_URL = "https://campusquality.tum.de/QSYSTEM_TUM/"

        private var client: TUMOnlineClient? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): TUMOnlineClient {
            if (client == null) {
                client = buildAPIClient(context)
            }

            return client!!
        }

        private fun buildAPIClient(context: Context): TUMOnlineClient {
            val cacheManager = CacheManager(context)

            val client = ApiHelper.getOkHttpClient(context)
                    .newBuilder()
                    .cache(cacheManager.cache)
                    .addInterceptor(AddTokenInterceptor(context))
                    .addInterceptor(CheckTokenInterceptor(context))
                    .addNetworkInterceptor(CacheResponseInterceptor())
                    .addNetworkInterceptor(CheckErrorInterceptor(context))
                    .build()

            val tikXml = TikXml.Builder()
                    .exceptionOnUnreadXml(false)
                    .build()
            val xmlConverterFactory = TikXmlConverterFactory.create(tikXml)

            val apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(xmlConverterFactory)
                    .build()
                    .create(TUMOnlineAPIService::class.java)
            return TUMOnlineClient(apiService)
        }

    }

}