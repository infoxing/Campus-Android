package de.tum.`in`.tumcampusapp.component.ui.overview.card

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.navigation.NavigationManager
import de.tum.`in`.tumcampusapp.component.other.settings.UserPreferencesActivity
import de.tum.`in`.tumcampusapp.component.ui.overview.CardInteractionListener
import de.tum.`in`.tumcampusapp.core.Const

open class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var currentCard: Card? = null

    private val context: Context by lazy { itemView.context }
    private val listener: CardInteractionListener? by lazy { context as? CardInteractionListener }

    protected val activity: AppCompatActivity by lazy { context as AppCompatActivity }

    init {
        itemView.setOnClickListener {
            val destination = currentCard?.getNavigationDestination()
            destination?.let { dest ->
                NavigationManager.open(activity, dest)
            }
        }

        val moreIcon = itemView.findViewById<ImageView>(R.id.cardMoreIcon)
        moreIcon?.setOnClickListener { openOptionsPopup(it) }
    }

    private fun openOptionsPopup(anchorView: View) {
        val card = currentCard ?: return

        PopupMenu(context, anchorView, Gravity.END).apply {
            menuInflater.inflate(card.optionsMenuResId, menu)
            setOnMenuItemClickListener { onOptionSelected(it) }
            show()
        }
    }

    private fun onOptionSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.open_card_setting -> openCardSettings()
            R.id.always_hide_card -> alwaysHideCard()
        }

        return true
    }

    private fun openCardSettings() {
        val key = currentCard?.settingsPrefix ?: return

        val intent = Intent(context, UserPreferencesActivity::class.java).apply {
            putExtra(Const.PREFERENCE_SCREEN, key)
        }
        context.startActivity(intent)
    }

    private fun alwaysHideCard() {
        currentCard?.hideAlways()
        currentCard?.discardCard()
        listener?.onAlwaysHideCard(adapterPosition)
    }

}