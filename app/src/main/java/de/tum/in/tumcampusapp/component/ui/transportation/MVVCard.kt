package de.tum.`in`.tumcampusapp.component.ui.transportation

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.navigation.SystemIntent
import de.tum.`in`.tumcampusapp.component.ui.overview.CardAdapter
import de.tum.`in`.tumcampusapp.component.ui.overview.CardManager.CARD_MVV
import de.tum.`in`.tumcampusapp.component.ui.overview.card.Card
import de.tum.`in`.tumcampusapp.component.ui.overview.card.CardViewHolder
import de.tum.`in`.tumcampusapp.component.ui.transportation.model.efa.Departure
import de.tum.`in`.tumcampusapp.component.ui.transportation.model.efa.StationResult

/**
 * Card that shows MVV departure times
 */
class MVVCard internal constructor(context: Context,
                                   val station: StationResult,
                                   val departures: List<Departure>) :
        Card(CARD_MVV, context, "card_mvv") {

    val title = station.station

    override val optionsMenuResId = R.menu.card_popup_menu

    override fun updateViewHolder(viewHolder: RecyclerView.ViewHolder) {
        super.updateViewHolder(viewHolder)

        if (viewHolder is MVVCardViewHolder) {
            viewHolder.bind(station, departures)
        }
    }

    override fun getNavigationDestination() = SystemIntent(station.getIntent(context))

    override fun discard(editor: Editor) {
        editor.putLong(MVV_TIME, System.currentTimeMillis())
    }

    override fun shouldShow(prefs: SharedPreferences): Boolean {
        val prevDate = prefs.getLong(MVV_TIME, 0)
        return prevDate + DateUtils.HOUR_IN_MILLIS < System.currentTimeMillis()
    }

    companion object : CardAdapter.CardViewHolderFactory {
        private const val MVV_TIME = "mvv_time"

        override fun inflateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            val view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.card_mvv, parent, false)
            return MVVCardViewHolder(view)
        }
    }

}
