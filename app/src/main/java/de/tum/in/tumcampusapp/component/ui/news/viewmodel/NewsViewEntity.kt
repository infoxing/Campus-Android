package de.tum.`in`.tumcampusapp.component.ui.news.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import de.tum.`in`.tumcampusapp.model.news.News
import de.tum.`in`.tumcampusapp.component.ui.tufilm.KinoActivity
import de.tum.`in`.tumcampusapp.utils.Const
import de.tum.`in`.tumcampusapp.utils.DateTimeUtils
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

data class NewsViewEntity(
        val id: String,
        val title: String,
        val link: String,
        val src: String,
        val imageUrl: String,
        val date: DateTime,
        val formattedDate: String,
        val created: DateTime,
        val dismissed: Int,
        val isFilm: Boolean,
        val isNewspread: Boolean
) {

    fun getIntent(context: Context): Intent? {
        return if (isFilm) {
            Intent(context, KinoActivity::class.java).apply {
                putExtra(Const.KINO_DATE, DateTimeUtils.getDateTimeString(date))
            }
        } else {
            if (link.isBlank()) null else Intent(Intent.ACTION_VIEW, Uri.parse(link))
        }
    }

    companion object {

        @JvmStatic
        fun create(news: News): NewsViewEntity {
            val dateFormatter = DateTimeFormat.mediumDate()
            val formattedDate = dateFormatter.print(news.date)

            val isFilm = news.src == 2.toString()
            val isNewspread = setOf(7, 8, 9, 13).contains(news.src.toInt())

            return NewsViewEntity(
                    news.id, news.title, news.link, news.src, news.image, news.date,
                    formattedDate, news.created, news.dismissed, isFilm, isNewspread
            )
        }

    }

}
