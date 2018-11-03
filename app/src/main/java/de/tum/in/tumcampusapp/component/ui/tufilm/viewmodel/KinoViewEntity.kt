package de.tum.`in`.tumcampusapp.component.ui.tufilm.viewmodel

import android.os.Parcel
import android.os.Parcelable
import de.tum.`in`.tumcampusapp.model.tufilm.RawKino
import org.joda.time.format.DateTimeFormat
import java.util.*

data class KinoViewEntity(
        val id: String,
        val title: String,
        val year: String,
        val runtime: String,
        val genre: String,
        val director: String,
        val actors: String,
        val rating: String,
        val description: String,
        val coverUrl: String,
        val trailerUrl: String,
        val formattedDate: String,
        val link: String,
        val isFutureMovie: Boolean
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte()
    )

    companion object {

        @JvmField
        var CREATOR = object : Parcelable.Creator<KinoViewEntity> {
            override fun createFromParcel(parcel: Parcel): KinoViewEntity {
                return KinoViewEntity(parcel)
            }

            override fun newArray(size: Int): Array<KinoViewEntity?> {
                return arrayOfNulls(size)
            }
        }

        @JvmStatic
        fun create(kino: RawKino): KinoViewEntity {
            val formatter = DateTimeFormat.mediumDate().withLocale(Locale.getDefault())
            val formattedDate = formatter.print(kino.date)

            val actualTitle = kino.title.split(": ")[1]
            val trailerSearchUrl = "https://www.youtube.com/results?search_query=Trailer $actualTitle".replace(" ", "+")

            val formattedDescription = kino.description
                    .replace("\n", "")
                    .replace("\r", "\r\n")
                    .removeSuffix("\r\n")

            return KinoViewEntity(
                    kino.id, kino.title, kino.year, kino.runtime, kino.genre, kino.director,
                    kino.actors, kino.rating, formattedDescription, kino.cover, trailerSearchUrl,
                    formattedDate, kino.link, kino.date.isAfterNow
            )

        }

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(year)
        parcel.writeString(runtime)
        parcel.writeString(genre)
        parcel.writeString(director)
        parcel.writeString(actors)
        parcel.writeString(rating)
        parcel.writeString(description)
        parcel.writeString(coverUrl)
        parcel.writeString(trailerUrl)
        parcel.writeString(formattedDate)
        parcel.writeString(link)
        parcel.writeByte(if (isFutureMovie) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

}
