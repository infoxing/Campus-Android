package de.tum.`in`.tumcampusapp.component.tumui.grades.viewmodel

import android.content.Context
import androidx.core.content.ContextCompat
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.generic.adapter.SimpleStickyListHeadersAdapter
import de.tum.`in`.tumcampusapp.model.grades.Exam
import de.tum.`in`.tumcampusapp.utils.tryOrNull
import org.joda.time.DateTime
import java.text.NumberFormat
import java.util.Locale.GERMAN

data class ExamViewEntity(
        val course: String,
        val credits: String? = null,
        val date: DateTime? = null, // todo formatted
        val examiner: String? = null,
        val grade: String? = null,
        val modus: String? = null,
        val programID: String,
        val semester: String,
        val isPassed: Boolean,
        val gradeColor: Int
) : Comparable<ExamViewEntity>, SimpleStickyListHeadersAdapter.SimpleStickyListItem {

    override fun getHeadName() = semester

    override fun getHeaderId() = semester

    override fun compareTo(other: ExamViewEntity): Int {
        return compareByDescending<ExamViewEntity> { it.semester }
                .thenByDescending { it.date }
                .thenBy { it.course }
                .compare(this, other)
    }

    companion object {

        @JvmStatic
        fun create(context: Context, exam: Exam): ExamViewEntity {
            val gradeValue = tryOrNull { NumberFormat.getInstance(GERMAN).parse(exam.grade).toDouble() } ?: 5.0
            val isPassed = gradeValue <= 4.0

            val resId = Exam.GRADE_COLORS[exam.grade] ?: R.color.grade_default
            val gradeColor = ContextCompat.getColor(context, resId)

            return ExamViewEntity(
                    exam.course, exam.credits, exam.date, exam.examiner, exam.grade, exam.modus,
                    exam.programID, exam.semester, isPassed, gradeColor
            )
        }

    }

}
