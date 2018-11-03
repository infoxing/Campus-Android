package de.tum.`in`.tumcampusapp.ui.ticket

import androidx.recyclerview.widget.DiffUtil
import de.tum.`in`.tumcampusapp.ui.ticket.viewmodel.EventViewEntity

class EventDiffUtil(
        private val oldItems: List<EventViewEntity>,
        private val newItems: List<EventViewEntity>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].id == newItems[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition] == newItems[newItemPosition]
    }

}
