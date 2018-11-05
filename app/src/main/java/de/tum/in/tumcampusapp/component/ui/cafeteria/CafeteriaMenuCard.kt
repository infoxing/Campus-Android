package de.tum.`in`.tumcampusapp.component.ui.cafeteria

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.navigation.NavigationDestination
import de.tum.`in`.tumcampusapp.component.other.navigation.SystemActivity
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.activity.CafeteriaActivity
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.model.CafeteriaWithMenus
import de.tum.`in`.tumcampusapp.component.ui.overview.CardAdapter
import de.tum.`in`.tumcampusapp.component.ui.overview.CardManager
import de.tum.`in`.tumcampusapp.component.ui.overview.card.Card
import de.tum.`in`.tumcampusapp.component.ui.overview.card.CardViewHolder
import de.tum.`in`.tumcampusapp.utils.Const

/**
 * Card that shows the cafeteria menu
 */
class CafeteriaMenuCard(context: Context, val cafeteria: CafeteriaWithMenus) :
        Card(CardManager.CARD_CAFETERIA, context, "card_cafeteria") {

    override val optionsMenuResId = R.menu.card_popup_menu

    val title = cafeteria.name

    override fun updateViewHolder(viewHolder: RecyclerView.ViewHolder) {
        super.updateViewHolder(viewHolder)

        if (viewHolder is CafeteriaMenuViewHolder) {
            viewHolder.bind(cafeteria)
        }
    }

    override fun getNavigationDestination(): NavigationDestination {
        val bundle = Bundle()
        bundle.putInt(Const.CAFETERIA_ID, cafeteria.id)
        return SystemActivity(CafeteriaActivity::class.java, bundle)
    }

    override fun discard(editor: Editor) {
        editor.putLong(CAFETERIA_DATE, cafeteria.nextMenuDate.millis)
    }

    override fun shouldShow(prefs: SharedPreferences): Boolean {
        val prevDate = prefs.getLong(CAFETERIA_DATE, 0)
        val date = cafeteria.nextMenuDate
        return prevDate < date.millis
    }

    companion object : CardAdapter.CardViewHolderFactory {
        private const val CAFETERIA_DATE = "cafeteria_date"

        override fun inflateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            val view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.card_cafeteria_menu, parent, false)
            return CafeteriaMenuViewHolder(view)
        }
    }
}
