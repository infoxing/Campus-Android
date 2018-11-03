package de.tum.`in`.tumcampusapp.component.tumui.person.viewmodel

import de.tum.`in`.tumcampusapp.model.person.Person
import de.tum.`in`.tumcampusapp.model.person.Gender
import java.io.Serializable

data class PersonViewEntity(
        val gender: Gender,
        val id: String,
        val name: String,
        val surname: String,
        val fullName: String,
        val raw: Person
) : Serializable {

    companion object {

        @JvmStatic
        fun create(person: Person): PersonViewEntity {
            val fullName = "$person.name $person.surname"
            return PersonViewEntity(
                    person.gender, person.id, person.name,
                    person.surname, fullName, person
            )
        }

    }

}