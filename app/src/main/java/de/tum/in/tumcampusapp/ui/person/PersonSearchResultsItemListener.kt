package de.tum.`in`.tumcampusapp.ui.person

import de.tum.`in`.tumcampusapp.ui.person.viewmodel.PersonViewEntity

interface PersonSearchResultsItemListener {

    fun onItemSelected(person: PersonViewEntity)

}