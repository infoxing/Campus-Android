package de.tum.`in`.tumcampusapp.component.tumui.calendar

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.tumui.calendar.model.CalendarItem
import de.tum.`in`.tumcampusapp.component.ui.overview.CardAdapter
import de.tum.`in`.tumcampusapp.component.ui.overview.CardManager
import de.tum.`in`.tumcampusapp.component.ui.overview.card.Card
import de.tum.`in`.tumcampusapp.component.ui.overview.card.CardViewHolder
import org.joda.time.DateTime

class NextLectureCard internal constructor(context: Context) :
        Card(CardManager.CARD_NEXT_LECTURE, context, "card_next_lecture") {
    private val calendarController: CalendarController = CalendarController(context)

    private val lectures = mutableListOf<CardCalendarItem>()

    override val optionsMenuResId = R.menu.card_popup_menu

    override fun updateViewHolder(viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is NextLectureCardViewHolder) {
            viewHolder.bind(lectures)
        }
    }

    override fun discard(editor: SharedPreferences.Editor) {
        val item = lectures[lectures.size - 1]
        editor.putLong(NEXT_LECTURE_DATE, item.start.millis)
    }

    override fun shouldShow(prefs: SharedPreferences): Boolean {
        val item = lectures[0]
        val prevTime = prefs.getLong(NEXT_LECTURE_DATE, 0)
        return item.start.millis > prevTime
    }

    override fun getId() = 0

    fun setLectures(calendarItems: List<CalendarItem>) {
        calendarItems.mapTo(lectures) {
            CardCalendarItem(
                    id = it.nr,
                    start = it.dtstart,
                    end = it.dtend,
                    title = it.getFormattedTitle(),
                    locations = calendarController.getLocationsForEvent(it.nr).orEmpty()
            )
        }
    }

    data class CardCalendarItem(var id: String,
                                var title: String,
                                var start: DateTime,
                                var end: DateTime,
                                var locations: List<String>) {

        internal val locationString: String
            get() = locations.joinToString("\n")
    }

    companion object : CardAdapter.CardViewHolderFactory {
        private const val NEXT_LECTURE_DATE = "next_date"

        override fun inflateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_next_lecture_item, parent, false)
            return NextLectureCardViewHolder(view)
        }
    }

}
