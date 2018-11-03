package de.tum.`in`.tumcampusapp.model.calendar

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import de.tum.`in`.tumcampusapp.model.converters.DateTimeConverter
import de.tum.`in`.tumcampusapp.model.locations.Geo
import org.joda.time.DateTime

@Xml(name = "event")
data class Event(
        @PropertyElement(name = "description") val description: String? = null,
        @PropertyElement(name = "dtstart", converter = DateTimeConverter::class) val startTime: DateTime? = null,
        @PropertyElement(name = "dtend", converter = DateTimeConverter::class) val endTime: DateTime? = null,
        @PropertyElement(name = "geo") val geo: Geo? = null,
        @PropertyElement(name = "location") val location: String? = null,
        @PropertyElement(name = "nr") val id: String? = null,
        @PropertyElement(name = "status") val status: String? = null,
        @PropertyElement(name = "title") val title: String,
        @PropertyElement(name = "url") val url: String? = null) {

    /**
     * Retrieve related values for calendar item as CalendarItem object
     */
    fun toCalendarItem(): CalendarItem {
        return CalendarItem(
                id ?: "", status ?: "", url ?: "", title,
                description ?: "", startTime ?: DateTime(),
                endTime ?: DateTime(), location ?: "", false
        )
    }

}
