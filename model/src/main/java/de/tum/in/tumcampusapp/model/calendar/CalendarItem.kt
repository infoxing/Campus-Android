package de.tum.`in`.tumcampusapp.model.calendar

import android.content.ContentValues
import android.provider.CalendarContract
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.joda.time.DateTime

/**
 * Entity for storing information about lecture events
 */
@Entity(tableName = "calendar")
data class CalendarItem(
        @PrimaryKey
        var nr: String = "",
        var status: String = "",
        var url: String = "",
        var title: String = "",
        var description: String = "",
        var dtstart: DateTime = DateTime(),
        var dtend: DateTime = DateTime(),
        var location: String = "",
        @Ignore
        var blacklisted: Boolean = false
) {

    /**
     * Prepares ContentValues object with related values plugged
     */
    fun toContentValues(): ContentValues {
        val values = ContentValues()

        // Put the received values into a contentResolver to
        // transmit the to Google Calendar
        values.put(CalendarContract.Events.DTSTART, dtstart.millis)
        values.put(CalendarContract.Events.DTEND, dtend.millis)
        values.put(CalendarContract.Events.TITLE, title)
        values.put(CalendarContract.Events.DESCRIPTION, description)
        values.put(CalendarContract.Events.EVENT_LOCATION, location)
        return values
    }

    fun isSameEventButForLocation(other: CalendarItem): Boolean {
        return title == other.title && dtstart == other.dtstart && dtend == other.dtend
    }

}
