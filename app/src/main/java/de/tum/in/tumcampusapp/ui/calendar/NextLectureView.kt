package de.tum.`in`.tumcampusapp.ui.calendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.core.DateTimeUtils
import kotlinx.android.synthetic.main.layout_card_lecture.view.*


class NextLectureView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val view = LayoutInflater.from(context).inflate(R.layout.layout_card_lecture, this, true)

    fun setLecture(lecture: NextLectureCard.CardCalendarItem) {
        view.lectureTitleTextView.text = lecture.title
        view.lectureTimeTextView.text = DateTimeUtils.formatFutureTime(lecture.start, context)

        if (lecture.locations == null || lecture.locations.isEmpty()) {
            lectureLocationTextView.visibility = View.GONE
        } else {
            lectureLocationTextView.visibility = View.VISIBLE
            lectureLocationTextView.text = lecture.locationString
        }

        view.setOnClickListener {
            openEventBottomSheet(lecture)
        }
    }

    private fun openEventBottomSheet(item: NextLectureCard.CardCalendarItem) {
        val detailsFragment =
                CalendarDetailsFragment.newInstance(item.id, isShownInCalendarActivity = false)
        val activity = context as AppCompatActivity
        detailsFragment.show(activity.supportFragmentManager, null)
    }

}
