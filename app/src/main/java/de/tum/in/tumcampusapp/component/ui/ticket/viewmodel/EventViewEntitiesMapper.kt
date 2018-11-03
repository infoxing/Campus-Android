package de.tum.`in`.tumcampusapp.component.ui.ticket.viewmodel

import android.content.Context
import androidx.arch.core.util.Function
import de.tum.`in`.tumcampusapp.component.ui.ticket.model.RawEvent

class EventViewEntitiesMapper(
        private val context: Context
) : Function<List<RawEvent>, List<EventViewEntity>> {

    override fun apply(input: List<RawEvent>): List<EventViewEntity> {
        return input.map { EventViewEntity.create(context, it) }
    }

}
