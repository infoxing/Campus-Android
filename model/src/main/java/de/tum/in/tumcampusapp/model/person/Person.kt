package de.tum.`in`.tumcampusapp.model.person

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import de.tum.`in`.tumcampusapp.model.converters.GenderConverter
import de.tum.`in`.tumcampusapp.model.recents.Recent
import java.io.Serializable

/**
 * A person, often an [Employee] working at TUM.
 *
 *
 * Note: This model is based on the TUMOnline web service response format for a
 * corresponding request.
 */
@Xml(name = "row")
data class Person(
        @PropertyElement(name = "geschlecht", converter = GenderConverter::class)
        var gender: Gender = Gender.UNKNOWN,
        @PropertyElement(name = "obfuscated_id")
        var id: String = "",
        @PropertyElement(name = "vorname")
        var name: String = "",
        @PropertyElement(name = "familienname")
        var surname: String = ""
) : Serializable {

    companion object {

        private const val serialVersionUID = -5210814076506102292L

        @JvmStatic fun fromRecent(r: Recent): Person {
            val split = r.name.split("\\$".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val p = Person()
            p.id = split[0]
            p.name = split[1]
            return p
        }

    }

}
