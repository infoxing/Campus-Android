package de.tum.`in`.tumcampusapp.model.person

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import de.tum.`in`.tumcampusapp.model.converters.GenderConverter

/**
 * An employee of the TUM.
 *
 *
 * Note: This model is based on the TUMOnline web service response format for a
 * corresponding request.
 */
@Xml(name = "person")
data class Employee(
        @PropertyElement(name = "geschlecht", converter = GenderConverter::class)
        val gender: Gender? = null,
        @PropertyElement(name = "obfuscated_id")
        val id: String = "",
        @PropertyElement(name = "vorname")
        val name: String = "",
        @PropertyElement(name = "familienname")
        val surname: String = "",
        @Element(name = "dienstlich")
        val businessContact: Contact? = null,
        @PropertyElement(name = "sprechstunde")
        val consultationHours: String = "",
        @PropertyElement
        val email: String = "",
        @Element(name = "gruppen")
        val groupList: GroupList? = null,
        @PropertyElement(name = "image_data")
        val imageData: String = "",
        @Element(name = "privat")
        val privateContact: Contact? = null,
        @Element(name = "raeume")
        val roomList: RoomList? = null,
        @Element(name = "telefon_nebenstellen")
        val telSubstationList: TelSubstationList? = null,
        @PropertyElement(name = "titel")
        val title: String = ""
)
