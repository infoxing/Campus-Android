package de.tum.`in`.tumcampusapp.component.ui.overview

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tum.`in`.tumcampusapp.component.tumui.calendar.NextLectureCard
import de.tum.`in`.tumcampusapp.component.tumui.tutionfees.TuitionFeesCard
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.CafeteriaMenuCard
import de.tum.`in`.tumcampusapp.component.ui.chat.ChatMessagesCard
import de.tum.`in`.tumcampusapp.component.ui.eduroam.EduroamCard
import de.tum.`in`.tumcampusapp.component.ui.eduroam.EduroamFixCard
import de.tum.`in`.tumcampusapp.component.ui.news.NewsCard
import de.tum.`in`.tumcampusapp.component.ui.news.TopNewsCard
import de.tum.`in`.tumcampusapp.component.ui.onboarding.LoginPromptCard
import de.tum.`in`.tumcampusapp.component.ui.overview.CardManager.CardType
import de.tum.`in`.tumcampusapp.component.ui.overview.card.Card
import de.tum.`in`.tumcampusapp.component.ui.overview.card.CardViewHolder
import de.tum.`in`.tumcampusapp.component.ui.ticket.EventCard
import de.tum.`in`.tumcampusapp.component.ui.transportation.MVVCard
import java.util.*

/**
 * Adapter for the cards start page used in [MainActivity]
 */
class CardAdapter : RecyclerView.Adapter<CardViewHolder>() {

    interface CardViewHolderFactory {
        fun inflateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder
    }

    private val items = ArrayList<Card>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, @CardType viewType: Int): CardViewHolder {
        return when (viewType) {
            CardManager.CARD_CAFETERIA -> CafeteriaMenuCard
            CardManager.CARD_TUITION_FEE -> TuitionFeesCard
            CardManager.CARD_NEXT_LECTURE -> NextLectureCard
            CardManager.CARD_RESTORE -> RestoreCard
            CardManager.CARD_NO_INTERNET -> NoInternetCard
            CardManager.CARD_MVV -> MVVCard
            CardManager.CARD_NEWS,
            CardManager.CARD_NEWS_FILM -> NewsCard
            CardManager.CARD_EDUROAM -> EduroamCard
            CardManager.CARD_EDUROAM_FIX -> EduroamFixCard
            CardManager.CARD_CHAT -> ChatMessagesCard
            CardManager.CARD_SUPPORT -> SupportCard
            CardManager.CARD_LOGIN -> LoginPromptCard
            CardManager.CARD_TOP_NEWS -> TopNewsCard
            CardManager.CARD_EVENT -> EventCard
            else -> throw UnsupportedOperationException()
        }.inflateViewHolder(viewGroup, viewType)
    }

    override fun onBindViewHolder(viewHolder: CardViewHolder, position: Int) {
        val card = items[position]
        viewHolder.currentCard = card
        card.updateViewHolder(viewHolder)
    }

    override fun getItemViewType(position: Int) = items[position].cardType

    override fun getItemId(position: Int): Long {
        val card = items[position]
        return (card.cardType + (card.getId() shl 4)).toLong()
    }

    override fun getItemCount() = items.size

    fun updateItems(newCards: List<Card>) {
        val diffResult = DiffUtil.calculateDiff(Card.DiffCallback(items, newCards))

        items.clear()
        items.addAll(newCards)

        diffResult.dispatchUpdatesTo(this)
    }

    fun remove(position: Int): Card {
        val card = items.removeAt(position)
        notifyItemRemoved(position)
        return card
    }

    fun insert(position: Int, card: Card) {
        items.add(position, card)
        notifyItemInserted(position)
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        val targetPosition = validatePosition(fromPosition, toPosition)
        val card = items.removeAt(fromPosition)
        items.add(targetPosition, card)

        // Update card positions so they stay the same even when the app is closed
        for (index in items.indices) {
            items[index].position = index
        }
        notifyItemMoved(fromPosition, targetPosition)
    }

    private fun validatePosition(fromPosition: Int, toPosition: Int): Int {
        val selectedCard = items[fromPosition]
        val cardAtPosition = items[toPosition]

        // If there is a support card, it should always be the first one
        // except when it's been dismissed.
        // Restore card should stay at the bottom
        return when (selectedCard) {
            is RestoreCard,
            is SupportCard -> fromPosition
            else ->
                when (cardAtPosition) {
                    is SupportCard -> toPosition + 1
                    is RestoreCard -> toPosition - 1
                    else -> toPosition
                }
        }
    }
}
