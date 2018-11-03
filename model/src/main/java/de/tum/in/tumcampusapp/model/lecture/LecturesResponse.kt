package de.tum.`in`.tumcampusapp.model.lecture

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

/**
 * This class is dealing with the deserialization of the output of TUMOnline to
 * the method "sucheLehrveranstaltungen" or "eigeneLehrveranstaltungen".
 *
 * @see Lecture
 */
@Xml(name = "rowset")
data class LecturesResponse(@Element val lectures: List<Lecture> = mutableListOf())
