package de.tum.`in`.tumcampusapp.component.tumui.person

import de.tum.`in`.tumcampusapp.component.tumui.person.viewmodel.PersonViewEntity

interface PersonSearchResultsItemListener {

    fun onItemSelected(person: PersonViewEntity)

}