package de.tum.`in`.tumcampusapp.component.ui.ticket.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.squareup.picasso.Picasso
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.api.app.TUMCabeClient
import de.tum.`in`.tumcampusapp.api.tumonline.AccessTokenManager
import de.tum.`in`.tumcampusapp.component.tumui.calendar.CreateEventActivity
import de.tum.`in`.tumcampusapp.component.ui.ticket.EventsController
import de.tum.`in`.tumcampusapp.component.ui.ticket.activity.BuyTicketActivity
import de.tum.`in`.tumcampusapp.component.ui.ticket.activity.ShowTicketActivity
import de.tum.`in`.tumcampusapp.component.ui.ticket.model.RawEvent
import de.tum.`in`.tumcampusapp.component.ui.ticket.payload.TicketStatus
import de.tum.`in`.tumcampusapp.component.ui.ticket.viewmodel.EventViewEntity
import de.tum.`in`.tumcampusapp.utils.Const
import de.tum.`in`.tumcampusapp.utils.Const.KEY_EVENT_ID
import de.tum.`in`.tumcampusapp.utils.DateTimeUtils
import de.tum.`in`.tumcampusapp.utils.Utils
import de.tum.`in`.tumcampusapp.utils.into
import kotlinx.android.synthetic.main.fragment_event_details.*
import kotlinx.android.synthetic.main.fragment_event_details.view.*
import org.joda.time.DateTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Fragment for displaying information about an [RawEvent]. Manages content that's shown in the
 * PagerAdapter.
 */
class EventDetailsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private val eventsController: EventsController by lazy {
        EventsController(requireContext())
    }

    private val rawEvent: RawEvent by lazy {
        arguments?.getParcelable(Const.KEY_EVENT) ?: throw IllegalArgumentException()
    }

    private val event: EventViewEntity by lazy {
        EventViewEntity.create(requireContext(), rawEvent)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(container?.context)
                .inflate(R.layout.fragment_event_details, container, false)

        view.swipeRefreshLayout.setOnRefreshListener(this)
        view.swipeRefreshLayout.setColorSchemeResources(
                R.color.color_primary,
                R.color.tum_A100,
                R.color.tum_A200
        )

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showEventDetails()
        loadAvailableTicketCount()
    }

    override fun onRefresh() {
        loadAvailableTicketCount()
    }

    override fun onResume() {
        super.onResume()
        if (!eventsController.isEventBooked(event.id) && isEventImminent(event.startTime)) {
            ticketButton.visibility = View.GONE
        }
    }

    /**
     * Checks if the event starts less than 4 hours from now.
     * (-> user won't be able to buy tickets anymore)
     */
    private fun isEventImminent(startTime: DateTime): Boolean {
        return DateTime.now().isAfter(startTime.minusHours(4))
    }

    private fun showEventImminentDialog() {
        context?.let {
            val dialog = AlertDialog.Builder(it)
                    .setTitle(R.string.error)
                    .setMessage(R.string.event_imminent_error)
                    .setPositiveButton(R.string.ok, null)
                    .create()
            dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_corners_background)
            dialog.show()
        }
    }

    private fun showEventDetails() {
        val url = event.imageUrl
        if (url != null) {
            Picasso.get()
                    .load(url)
                    .noPlaceholder()
                    .into(posterView) {
                        posterProgressBar?.visibility = View.GONE
                    }
        } else {
            posterProgressBar.visibility = View.GONE
        }

        if (eventsController.isEventBooked(event.id)) {
            ticketButton.text = getString(R.string.show_ticket)
            ticketButton.setOnClickListener { showTicket() }
        } else {
            ticketButton.text = getString(R.string.buy_ticket)
            ticketButton.setOnClickListener { buyTicket() }
        }

        context?.let {
            dateTextView.text = event.formattedStartTime
            dateContainer.setOnClickListener { displayAddToCalendarDialog() }
        }

        locationTextView.text = event.locality
        locationContainer.setOnClickListener { openMaps() }

        descriptionTextView.text = event.description

        linkButton.setOnClickListener { openEventLink() }
        linkButton.visibility = if (event.eventUrl.isNotBlank()) View.VISIBLE else View.GONE
    }

    private fun openEventLink() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.eventUrl))
        startActivity(intent)
    }

    private fun loadAvailableTicketCount() {
        TUMCabeClient
                .getInstance(context)
                .fetchTicketStats(event.id, object : Callback<List<TicketStatus>> {
                    override fun onResponse(call: Call<List<TicketStatus>>,
                                            response: Response<List<TicketStatus>>) {
                        val statuses = response.body() ?: return
                        val sum = statuses.sumBy { it.availableTicketCount }

                        val text = String.format(Locale.getDefault(), "%d", sum)

                        if (isDetached.not()) {
                            remainingTicketsTextView.text = text
                            swipeRefreshLayout.isRefreshing = false
                        }
                    }

                    override fun onFailure(call: Call<List<TicketStatus>>, t: Throwable) {
                        Utils.log(t)

                        if (isDetached.not()) {
                            remainingTicketsTextView.setText(R.string.unknown)
                            swipeRefreshLayout.isRefreshing = false
                        }
                    }
                })
    }

    private fun showTicket() {
        val intent = Intent(context, ShowTicketActivity::class.java).apply {
            putExtra(KEY_EVENT_ID, event.id)
        }
        startActivity(intent)
    }

    private fun buyTicket() {
        if (isEventImminent(event.startTime)) {
            showEventImminentDialog()
            ticketButton.visibility = View.GONE
            return
        }

        val lrzId = Utils.getSetting(requireContext(), Const.LRZ_ID, "")
        val chatRoomName = Utils.getSetting(requireContext(), Const.CHAT_ROOM_DISPLAY_NAME, "")
        val isLoggedIn = AccessTokenManager.hasValidAccessToken(context)

        if (!isLoggedIn || lrzId.isEmpty() || chatRoomName.isEmpty()) {
            context?.let {
                val dialog = AlertDialog.Builder(it)
                        .setTitle(R.string.error)
                        .setMessage(R.string.not_logged_in_error)
                        .setPositiveButton(R.string.ok) { _, _ -> activity?.finish() }
                        .create()

                dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_corners_background)
                dialog.show()
            }
            return
        }

        val intent = Intent(context, BuyTicketActivity::class.java).apply {
            putExtra(KEY_EVENT_ID, event.id)
        }
        startActivity(intent)
    }

    private fun addToTUMCalendar() {
        val event = event ?: return
        val endTime = event.endTime ?: event.startTime.plus(RawEvent.defaultDuration.toLong())

        val intent = Intent(context, CreateEventActivity::class.java).apply {
            putExtra(Const.EVENT_EDIT, false)
            putExtra(Const.EVENT_TITLE, event.title)
            putExtra(Const.EVENT_COMMENT, event.description)
            putExtra(Const.EVENT_START, event.startTime)
            putExtra(Const.EVENT_END, endTime)
        }

        startActivity(intent)
    }

    private fun addToExternalCalendar() {
        val event = event ?: return
        val endTime = event.endTime ?: event.startTime.plus(RawEvent.defaultDuration.toLong())
        val eventEnd = DateTimeUtils.getDateTimeString(endTime)

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.startTime.millis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, eventEnd)
            putExtra(CalendarContract.Events.TITLE, event.title)
            putExtra(CalendarContract.Events.DESCRIPTION, event.description)
            putExtra(CalendarContract.Events.EVENT_LOCATION, event.locality)
            // Indicates that this event is free time and will not conflict with other events
            putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE)
        }

        startActivity(intent)
    }

    private fun openMaps() {
        val url = "http://maps.google.co.in/maps?q=${event.locality}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun displayAddToCalendarDialog() {
        val context = requireContext()

        val calendars = arrayOf(
                getString(R.string.external_calendar),
                getString(R.string.tum_calendar)
        )

        val dialog = AlertDialog.Builder(context)
                .setTitle(R.string.add_to_calendar_info)
                .setSingleChoiceItems(calendars, 0, null)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.add) { _, which ->
                    handleCalendarExportSelection(which)
                }
                .setCancelable(true)
                .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_corners_background)
        dialog.show()
    }

    private fun handleCalendarExportSelection(which: Int) {
        when(which) {
            0 -> addToExternalCalendar()
            else -> addToTUMCalendar()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(event: RawEvent): EventDetailsFragment {
            return EventDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Const.KEY_EVENT, event)
                }
            }
        }

    }

}
