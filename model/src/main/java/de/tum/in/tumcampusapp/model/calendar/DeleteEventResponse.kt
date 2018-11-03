package de.tum.`in`.tumcampusapp.model.calendar

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "termin")
data class DeleteEventResponse(@PropertyElement(name = "delete") val delete: String = "")