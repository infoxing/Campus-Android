package de.tum.`in`.tumcampusapp.model.tuition

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import de.tum.`in`.tumcampusapp.model.converters.DateTimeConverter
import de.tum.`in`.tumcampusapp.model.converters.FloatConverter
import org.joda.time.DateTime

/**
 * Class holding tuition information.
 *
 *
 * Note: This model is based on the TUMOnline web service response format for a
 * corresponding request.
 */
@Xml(name = "row")
data class Tuition(
        @PropertyElement(name = "frist", converter = DateTimeConverter::class)
        val deadline: DateTime,
        @PropertyElement(name = "semester_bezeichnung")
        val semester: String,
        @PropertyElement(name = "soll", converter = FloatConverter::class)
        val amount: Float
) {

    val isPaid: Boolean
        get() = amount == 0f

}
