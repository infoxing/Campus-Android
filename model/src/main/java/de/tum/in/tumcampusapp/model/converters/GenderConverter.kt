package de.tum.`in`.tumcampusapp.model.converters

import com.tickaroo.tikxml.TypeConverter
import de.tum.`in`.tumcampusapp.model.person.Gender

class GenderConverter : TypeConverter<Gender> {

    override fun write(value: Gender) = value.description

    override fun read(value: String): Gender {
        return when (value) {
            "M" -> Gender.MALE
            "W" -> Gender.FEMALE
            else -> Gender.UNKNOWN
        }
    }

}
