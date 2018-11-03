package de.tum.`in`.tumcampusapp.model.calendar

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

/**
 * This class is dealing with the deserialization of the output of TUMOnline to
 * the method "sucheLehrveranstaltungen" or "eigeneLehrveranstaltungen".
 *
 * @see LecturesSearchRow
 */
@Xml(name = "events")
data class EventsResponse(@Element val events: List<Event>? = null)
