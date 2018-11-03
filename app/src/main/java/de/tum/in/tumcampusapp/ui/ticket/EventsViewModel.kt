package de.tum.`in`.tumcampusapp.ui.ticket

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.tum.`in`.tumcampusapp.model.ticket.RawEvent
import de.tum.`in`.tumcampusapp.ui.ticket.viewmodel.EventType
import de.tum.`in`.tumcampusapp.ui.ticket.viewmodel.EventViewEntitiesMapper

class EventsViewModel(
        private val controller: EventsController,
        private val type: EventType,
        mapper: EventViewEntitiesMapper
) : ViewModel() {

    val events: LiveData<List<RawEvent>>
        get() = when (type) {
            EventType.ALL -> controller.events
            else -> controller.bookedEvents
        }

    val eventViewEntities = Transformations.map(events, mapper)

    class Factory(
            private val controller: EventsController,
            private val eventType: EventType,
            private val mapper: EventViewEntitiesMapper
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST") // no good way around this
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return EventsViewModel(controller, eventType, mapper) as T
        }

    }

}
