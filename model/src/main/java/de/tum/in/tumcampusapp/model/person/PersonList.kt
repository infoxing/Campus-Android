package de.tum.`in`.tumcampusapp.model.person

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

/**
 * Wrapper class holding a list of persons. Note: This model is based on the
 * TUMOnline web service response format for a corresponding request.
 */

@Xml(name = "rowset")
data class PersonList(@Element val persons: List<Person> = mutableListOf())

