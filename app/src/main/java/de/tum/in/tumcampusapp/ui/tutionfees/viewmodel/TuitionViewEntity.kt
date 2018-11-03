package de.tum.`in`.tumcampusapp.ui.tutionfees.viewmodel

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.ui.tutionfees.TuitionFeesActivity
import de.tum.`in`.tumcampusapp.model.tuition.Tuition
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.text.ParseException
import java.util.*

data class TuitionViewEntity(
        val formattedDeadline: String,
        val longFormattedDeadline: String,
        val semester: String,
        val amount: Float,
        val formattedAmountText: String,
        val isPaid: Boolean,
        val color: Int
) {

    fun getIntent(context: Context): Intent = Intent(context, TuitionFeesActivity::class.java)

    companion object {

        @JvmStatic
        fun create(context: Context, tuition: Tuition): TuitionViewEntity {
            val amountText = try {
                val amountText = String.format(Locale.getDefault(), "%.2f", tuition.amount)
                "$amountText €"
            } catch (e: ParseException) {
                context.getString(R.string.not_available)
            }

            val formattedDeadline = DateTimeFormat.mediumDate().print(tuition.deadline)
            val longFormattedDeadline = DateTimeFormat.longDate().withLocale(Locale.getDefault()).print(tuition.deadline)
            val isPaid = tuition.amount == 0f

            val color = if (tuition.isPaid) {
                ContextCompat.getColor(context, R.color.sections_green)
            } else {
                val nextWeek = DateTime().plusWeeks(1)
                if (nextWeek.isAfter(tuition.deadline)) {
                    ContextCompat.getColor(context, R.color.error)
                } else {
                    ContextCompat.getColor(context, R.color.black)
                }
            }

            return TuitionViewEntity(
                    formattedDeadline, longFormattedDeadline,
                    tuition.semester, tuition.amount, amountText, isPaid, color
            )
        }

    }

}