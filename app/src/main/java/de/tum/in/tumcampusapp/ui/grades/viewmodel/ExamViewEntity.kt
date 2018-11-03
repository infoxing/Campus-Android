package de.tum.`in`.tumcampusapp.ui.grades.viewmodel

import android.content.Context
import androidx.core.content.ContextCompat
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.ui.generic.adapter.SimpleStickyListHeadersAdapter
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

            val resId = GRADE_COLORS[exam.grade] ?: R.color.grade_default
            val gradeColor = ContextCompat.getColor(context, resId)

            return ExamViewEntity(
                    exam.course, exam.credits, exam.date, exam.examiner, exam.grade, exam.modus,
                    exam.programID, exam.semester, isPassed, gradeColor
            )
        }

        val GRADE_COLORS = mapOf(
                "1,0" to R.color.grade_1_0,
                "1,3" to R.color.grade_1_3,
                "1,4" to R.color.grade_1_3,
                "1,7" to R.color.grade_1_7,
                "2,0" to R.color.grade_2_0,
                "2,3" to R.color.grade_2_3,
                "2,4" to R.color.grade_2_3,
                "2,7" to R.color.grade_2_7,
                "3,0" to R.color.grade_3_0,
                "3,3" to R.color.grade_3_3,
                "3,4" to R.color.grade_3_3,
                "3,7" to R.color.grade_3_7,
                "4,0" to R.color.grade_4_0,
                "4,3" to R.color.grade_4_3,
                "4,4" to R.color.grade_4_3,
                "4,7" to R.color.grade_4_7,
                "5,0" to R.color.grade_5_0
        )

    }

}
