package de.tum.`in`.tumcampusapp.component.ui.tufilm

import android.content.Context
import android.content.Intent

import de.tum.`in`.tumcampusapp.component.other.navigation.NavigationDestination
import de.tum.`in`.tumcampusapp.component.other.navigation.SystemIntent
import de.tum.`in`.tumcampusapp.component.ui.news.NewsCard
import de.tum.`in`.tumcampusapp.component.ui.news.model.News
import de.tum.`in`.tumcampusapp.component.ui.overview.CardManager
import de.tum.`in`.tumcampusapp.utils.Const
import de.tum.`in`.tumcampusapp.utils.DateTimeUtils

class FilmCard(context: Context, news: News) : NewsCard(CardManager.CARD_NEWS_FILM, context, news) {
    override fun getNavigationDestination(): NavigationDestination? {
        val intent = Intent(context, KinoActivity::class.java).apply {
            putExtra(Const.KINO_DATE, DateTimeUtils.getDateTimeString(date))
        }
        return SystemIntent(intent)
    }
}
