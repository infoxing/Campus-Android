package de.tum.`in`.tumcampusapp.model.news

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import com.google.gson.annotations.SerializedName


/**
 * This class contains information about the source of a [News] item.
 *
 * Find the currently available news sources at [https://app.tum.de/api/news/sources].
 *
 * @param id The ID of the news source
 * @param title The title of the news source
 * @param icon The imageUrl URL of the icon of the news source
 */
@Entity(tableName = "news_sources")
@SuppressWarnings(RoomWarnings.DEFAULT_CONSTRUCTOR)
data class NewsSources(
        @PrimaryKey
        @SerializedName("source")
        var id: Int = -1,
        var title: String = "",
        var icon: String = ""
)
