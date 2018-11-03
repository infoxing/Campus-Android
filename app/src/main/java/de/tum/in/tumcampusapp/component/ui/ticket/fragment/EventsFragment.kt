package de.tum.`in`.tumcampusapp.component.ui.ticket.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.generic.adapter.EqualSpacingItemDecoration
import de.tum.`in`.tumcampusapp.component.ui.ticket.EventsController
import de.tum.`in`.tumcampusapp.component.ui.ticket.EventsViewModel
import de.tum.`in`.tumcampusapp.component.ui.ticket.adapter.EventsAdapter
import de.tum.`in`.tumcampusapp.component.ui.ticket.model.RawEvent
import de.tum.`in`.tumcampusapp.component.ui.ticket.viewmodel.EventType
import de.tum.`in`.tumcampusapp.component.ui.ticket.model.RawTicket
import de.tum.`in`.tumcampusapp.component.ui.ticket.viewmodel.EventViewEntitiesMapper
import de.tum.`in`.tumcampusapp.component.ui.ticket.viewmodel.EventViewEntity
import de.tum.`in`.tumcampusapp.utils.Utils
import de.tum.`in`.tumcampusapp.utils.observeNonNull
import kotlinx.android.synthetic.main.fragment_events.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var eventType: EventType
    private lateinit var eventsController: EventsController

    private val eventsCallback = object : Callback<List<RawEvent>> {
        override fun onResponse(call: Call<List<RawEvent>>, response: Response<List<RawEvent>>) {
            val events = response.body() ?: return
            eventsController.storeEvents(events)

            if (this@EventsFragment.isDetached.not()) {
                eventsRefreshLayout.isRefreshing = false
            }
        }

        override fun onFailure(call: Call<List<RawEvent>>, t: Throwable) {
            val c = requireContext()
            Utils.log(t)
            Utils.showToast(c, R.string.error_something_wrong)

            if (this@EventsFragment.isDetached.not()) {
                eventsRefreshLayout.isRefreshing = false
            }
        }
    }

    private val ticketsCallback = object : Callback<List<RawTicket>> {
        override fun onResponse(call: Call<List<RawTicket>>, response: Response<List<RawTicket>>) {
            val tickets = response.body() ?: return
            eventsController.insert(*tickets.toTypedArray())
            eventsRefreshLayout.isRefreshing = false
        }

        override fun onFailure(call: Call<List<RawTicket>>, t: Throwable) {
            val c = requireContext()
            Utils.log(t)
            Utils.showToast(c, R.string.error_something_wrong)
            eventsRefreshLayout.isRefreshing = false
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(view) {
            eventsRecyclerView.setHasFixedSize(true)
            eventsRecyclerView.layoutManager = LinearLayoutManager(context)
            eventsRecyclerView.itemAnimator = DefaultItemAnimator()
            eventsRecyclerView.adapter = EventsAdapter(context)

            val spacing = Math.round(resources.getDimension(R.dimen.material_card_view_padding))
            eventsRecyclerView.addItemDecoration(EqualSpacingItemDecoration(spacing))

            eventsRefreshLayout.setOnRefreshListener(this@EventsFragment)
            eventsRefreshLayout.setColorSchemeResources(
                    R.color.color_primary,
                    R.color.tum_A100,
                    R.color.tum_A200
            )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        eventType = arguments?.getSerializable(KEY_EVENT_TYPE) as EventType
        eventsController = EventsController(context)

        val mapper = EventViewEntitiesMapper(requireContext())
        val factory = EventsViewModel.Factory(eventsController, eventType, mapper)
        val viewModel = ViewModelProviders.of(this, factory).get(EventsViewModel::class.java)

        viewModel.eventViewEntities.observeNonNull(this) { showEvents(it) }
    }

    private fun showEvents(events: List<EventViewEntity>) {
        val isEmpty = events.isEmpty()
        eventsRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
        placeholderTextView.visibility = if (isEmpty) View.VISIBLE else View.GONE

        if (events.isNotEmpty()) {
            val adapter = eventsRecyclerView.adapter as EventsAdapter
            adapter.update(events)
        } else {
            placeholderTextView.setText(eventType.placeholderResId)
        }
    }

    override fun onRefresh() {
        eventsController.getEventsAndTicketsFromServer(eventsCallback, ticketsCallback)
    }

    companion object {

        private const val KEY_EVENT_TYPE = "type"

        @JvmStatic
        fun newInstance(eventType: EventType): EventsFragment {
            return EventsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_EVENT_TYPE, eventType)
                }
            }
        }
    }

}
