package de.tum.`in`.tumcampusapp.component.ui.news

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.navigation.NavigationDestination
import de.tum.`in`.tumcampusapp.component.other.navigation.SystemIntent
import de.tum.`in`.tumcampusapp.component.ui.news.model.News
import de.tum.`in`.tumcampusapp.component.ui.overview.CardAdapter
import de.tum.`in`.tumcampusapp.component.ui.overview.CardManager
import de.tum.`in`.tumcampusapp.component.ui.overview.card.Card
import de.tum.`in`.tumcampusapp.component.ui.overview.card.CardViewHolder
import de.tum.`in`.tumcampusapp.utils.Utils
import org.joda.time.DateTime

/**
 * Card that shows selected news
 */
open class NewsCard(type: Int, context: Context, val news: News) :
        Card(type, context, "card_news") {

    override val optionsMenuResId: Int
        get() = R.menu.card_popup_menu

    val title: String
        get() = news.title

    val source: String
        get() = news.src

    val date: DateTime
        get() = news.date

    constructor(context: Context, news: News) : this(CardManager.CARD_NEWS, context, news)

    override fun getId() = Integer.parseInt(news.id)

    override fun updateViewHolder(viewHolder: RecyclerView.ViewHolder) {
        super.updateViewHolder(viewHolder)
        val holder = viewHolder as NewsViewHolder
        newsInflater.onBindNewsView(holder, news)
    }

    override fun shouldShow(prefs: SharedPreferences) = news.dismissed and 1 == 0

    override fun getNavigationDestination(): NavigationDestination? {
        val url = news.link
        if (url.isEmpty()) {
            Utils.showToast(context, R.string.no_link_existing)
            return null
        }

        val data = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        return SystemIntent(data)
    }

    override fun discard(editor: SharedPreferences.Editor) {
        NewsController(context).setDismissed(news.id, news.dismissed or 1)
    }

    companion object : CardAdapter.CardViewHolderFactory {
        private lateinit var newsInflater: NewsInflater

        override fun inflateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            newsInflater = NewsInflater(parent.context)
            return newsInflater.onCreateNewsView(parent, viewType, true)
        }
    }

}
