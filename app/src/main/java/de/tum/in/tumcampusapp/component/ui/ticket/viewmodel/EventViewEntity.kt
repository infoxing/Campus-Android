package de.tum.`in`.tumcampusapp.component.ui.ticket.viewmodel

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.format.DateFormat
import de.tum.`in`.tumcampusapp.model.ticket.RawEvent
import de.tum.`in`.tumcampusapp.model.readDateTime
import de.tum.`in`.tumcampusapp.model.writeDateTime
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

data class EventViewEntity(
        val id: Int,
        val imageUrl: String?,
        val title: String,
        val description: String,
        val locality: String,
        val startTime: DateTime,
        val formattedStartTime: String,
        val endTime: DateTime?,
        val eventUrl: String,
        val dismissed: Int
) : Parcelable, Comparable<EventViewEntity> {

    // Unsafe calls are only ok because we control writeToParcel().
    // Keep in sync with writeToParcel()!
    constructor(parcel: Parcel) : this(
            id = parcel.readInt(),
            imageUrl = parcel.readString(),
            title = parcel.readString(),
            description = parcel.readString(),
            locality = parcel.readString(),
            startTime = DateTime(parcel.readLong()),
            formattedStartTime = parcel.readString(),
            endTime = parcel.readDateTime(),
            eventUrl = parcel.readString(),
            dismissed = parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(imageUrl)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(locality)
        parcel.writeLong(startTime.millis)
        parcel.writeString(formattedStartTime)
        parcel.writeDateTime(endTime)
        parcel.writeString(eventUrl)
        parcel.writeInt(dismissed)
    }

    override fun compareTo(other: EventViewEntity): Int {
        return startTime.compareTo(other.startTime)
    }

    override fun describeContents() = 0

    companion object {

        @JvmField var CREATOR = object : Parcelable.Creator<EventViewEntity> {
            override fun createFromParcel(parcel: Parcel) = EventViewEntity(parcel)

            override fun newArray(size: Int) = arrayOfNulls<EventViewEntity?>(size)
        }

        @JvmStatic
        fun create(context: Context, event: RawEvent): EventViewEntity {
            val pattern = if (DateFormat.is24HourFormat(context)) "H:mm" else "h:mm aa"

            val startDate = DateTimeFormat.longDate().print(event.startTime)
            val startTime = DateTimeFormat.forPattern(pattern).print(event.startTime)
            val formattedStartDate = "$startDate, $startTime"

            return EventViewEntity(
                    event.id, event.imageUrl, event.title, event.description, event.locality,
                    event.startTime, formattedStartDate, event.endTime, event.eventUrl, event.dismissed
            )
        }

    }

}
