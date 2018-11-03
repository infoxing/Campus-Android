package de.tum.`in`.tumcampusapp.model.news

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

/**
 * New News
 *
 * @param id      News Facebook-ID
 * @param title   Title
 * @param link    Url, e.g. http://www.in.tum.de
 * @param image   Image url e.g. http://www.tu-film.de/img/film/poster/Fack%20ju%20Ghte.jpg
 * @param date    Date
 * @param created Creation date
 */
@Entity
@SuppressWarnings(RoomWarnings.DEFAULT_CONSTRUCTOR)
data class News(@PrimaryKey
                @SerializedName("news")
                var id: String = "",
                var title: String = "",
                var link: String = "",
                var src: String = "",
                var image: String = "",
                var date: DateTime = DateTime(),
                var created: DateTime = DateTime(),
                var dismissed: Int = 0) {

    val isFilm: Boolean
        get() = src == "2"

}
