package de.tum.`in`.tumcampusapp.ui.calendar.viewmodel

import android.content.Context
import androidx.core.content.ContextCompat
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.ui.calendar.IntegratedCalendarEvent
import de.tum.`in`.tumcampusapp.model.calendar.CalendarItem
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*
import java.util.regex.Pattern

data class CalendarItemViewEntity(
        val id: String,
        val status: String,
        val url: String,
        val title: String,
        val formattedTitle: String,
        val description: String,
        val startTime: DateTime,
        val endTime: DateTime,
        val formattedDate: String,
        val location: String,
        val formattedLocation: String,
        val isBlacklisted: Boolean,
        val isEditable: Boolean,
        val eventColor: Int,
        val isCanceled: Boolean
) {

    companion object {

        @JvmStatic
        fun create(context: Context, item: CalendarItem): CalendarItemViewEntity {
            val formattedTitle = Pattern.compile("[(\\[][A-Z0-9.]+[)\\]]")
                    // remove type of lecture (VO, UE, SE, PR) at the end of the line
                    .matcher(Pattern.compile(" (UE|VO|SE|PR)$")
                            .matcher(item.title)
                            .replaceAll(""))
                    .replaceAll("")
                    .trim { it <= ' ' }

            val timeFormat = DateTimeFormat.forPattern("HH:mm").withLocale(Locale.US)
            val dateFormat = DateTimeFormat.forPattern("EEE, dd.MM.yyyy").withLocale(Locale.US)

            val formattedDate = dateFormat.print(item.dtstart)
            val formattedStart = timeFormat.print(item.dtstart)
            val formattedEnd = timeFormat.print(item.dtend)
            val formattedDateTime = String.format("%s %s - %s", formattedDate, formattedStart, formattedEnd)

            val formattedLocation = Pattern.compile("\\([A-Z0-9\\.]+\\)")
                    .matcher(item.location)
                    .replaceAll("")!!
                    .trim { it <= ' ' }

            val isEditable = item.url.isBlank()
            val isCanceled = item.status == "CANCEL"

            val title = item.title
            val eventColorResId = if (isCanceled) {
                R.color.event_canceled
            } else if (title.endsWith("VO") || title.endsWith("VU")) {
                R.color.event_lecture
            } else if (title.endsWith("UE")) {
                R.color.event_exercise
            } else {
                R.color.event_other
            }

            val eventColor = ContextCompat.getColor(context, eventColorResId)
            val displayColor = IntegratedCalendarEvent.getDisplayColorFromColor(eventColor)

            return CalendarItemViewEntity(
                    item.nr, item.status, item.url, item.title, formattedTitle, item.description,
                    item.dtstart, item.dtend, formattedDateTime, item.location, formattedLocation,
                    item.blacklisted, isEditable, displayColor, isCanceled
            )
        }

    }

}
