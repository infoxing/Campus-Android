package de.tum.`in`.tumcampusapp.model.grades

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import de.tum.`in`.tumcampusapp.model.converters.DateTimeConverter
import org.joda.time.DateTime

/**
 * Exam passed by the user.
 *
 *
 * Note: This model is based on the TUMOnline web service response format for a
 * corresponding request.
 */
@Xml(name = "row")
data class Exam(
        @PropertyElement(name = "lv_titel")
        val course: String,
        @PropertyElement(name = "lv_credits")
        val credits: String? = null,
        @PropertyElement(name = "datum", converter = DateTimeConverter::class)
        val date: DateTime? = null,
        @PropertyElement(name = "pruefer_nachname")
        val examiner: String? = null,
        @PropertyElement(name = "uninotenamekurz")
        val grade: String? = null,
        @PropertyElement(name = "modus")
        val modus: String? = null,
        @PropertyElement(name = "studienidentifikator")
        val programID: String,
        @PropertyElement(name = "lv_semester")
        val semester: String = ""
) : Comparable<Exam> {

    override fun compareTo(other: Exam): Int {
        return compareByDescending<Exam> { it.semester }
                .thenByDescending { it.date }
                .thenBy { it.course }
                .compare(this, other)
    }

}