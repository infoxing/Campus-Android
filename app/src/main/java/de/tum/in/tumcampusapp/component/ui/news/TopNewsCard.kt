package de.tum.`in`.tumcampusapp.component.ui.news

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.navigation.NavigationDestination
import de.tum.`in`.tumcampusapp.component.other.navigation.SystemIntent
import de.tum.`in`.tumcampusapp.component.ui.overview.CardAdapter
import de.tum.`in`.tumcampusapp.component.ui.overview.CardManager
import de.tum.`in`.tumcampusapp.component.ui.overview.card.Card
import de.tum.`in`.tumcampusapp.component.ui.overview.card.CardViewHolder
import de.tum.`in`.tumcampusapp.utils.Const
import de.tum.`in`.tumcampusapp.utils.DateTimeUtils
import de.tum.`in`.tumcampusapp.utils.Utils

/**
 * Shows important news
 */
class TopNewsCard(context: Context) : Card(CardManager.CARD_TOP_NEWS, context, "top_news") {

    private fun updateImageView(imageView: ImageView, progress: ProgressBar) {
        val imageURL = Utils.getSetting(context, Const.NEWS_ALERT_IMAGE, "")
        if (imageURL.isEmpty()) {
            return
        }
        Picasso.get()
                .load(imageURL)
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        // remove progress bar
                        progress.visibility = View.GONE
                    }

                    override fun onError(e: Exception) = discardCard()
                })
    }

    override fun getId() = 0

    override fun getNavigationDestination(): NavigationDestination? {
        val url = Utils.getSetting(context, Const.NEWS_ALERT_LINK, "")
        if (url.isNotEmpty()) {
            return SystemIntent(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }

        // If there is no link, don't react to clicks
        return null
    }

    override fun updateViewHolder(viewHolder: RecyclerView.ViewHolder) {
        super.updateViewHolder(viewHolder)
        val imageView = viewHolder.itemView.findViewById<ImageView>(R.id.top_news_img)
        val progress = viewHolder.itemView.findViewById<ProgressBar>(R.id.top_news_progress)
        updateImageView(imageView, progress)
    }

    override fun shouldShow(prefs: SharedPreferences): Boolean {
        // don't show if the showUntil date does not exist or is in the past
        val untilDateString = Utils.getSetting(context, Const.NEWS_ALERT_SHOW_UNTIL, "")
        if (untilDateString.isEmpty()) {
            return false
        }

        val until = DateTimeUtils.parseIsoDateWithMillis(untilDateString) ?: return false
        return Utils.getSettingBool(context, CardManager.SHOW_TOP_NEWS, true) && until.isAfterNow
    }

    public override fun discard(editor: SharedPreferences.Editor) =
            Utils.setSetting(this.context, CardManager.SHOW_TOP_NEWS, false)

    companion object : CardAdapter.CardViewHolderFactory {
        override fun inflateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            return CardViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_top_news, parent, false))
        }
    }
}
