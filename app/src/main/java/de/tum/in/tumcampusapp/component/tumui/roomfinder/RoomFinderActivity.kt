package de.tum.`in`.tumcampusapp.component.tumui.roomfinder

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.api.app.TUMCabeClient
import de.tum.`in`.tumcampusapp.database.daos.RecentsDao
import de.tum.`in`.tumcampusapp.component.other.generic.activity.ActivityForSearchingInBackground
import de.tum.`in`.tumcampusapp.component.other.generic.adapter.NoResultsAdapter
import de.tum.`in`.tumcampusapp.component.tumui.roomfinder.viewmodel.RoomFinderRoomViewEntity
import de.tum.`in`.tumcampusapp.database.TcaDb
import de.tum.`in`.tumcampusapp.model.recents.Recent
import de.tum.`in`.tumcampusapp.model.roomfinder.RoomFinderRoom
import de.tum.`in`.tumcampusapp.core.NetUtils
import de.tum.`in`.tumcampusapp.core.Utils
import se.emilsjolander.stickylistheaders.StickyListHeadersListView
import java.io.IOException
import java.io.Serializable
import java.util.regex.Pattern

/**
 * Activity to show a convenience interface for using the MyTUM room finder.
 */
class RoomFinderActivity : ActivityForSearchingInBackground<List<RoomFinderRoomViewEntity>>(
        R.layout.activity_roomfinder, RoomFinderSuggestionProvider.AUTHORITY, 3
), OnItemClickListener {

    private val recentsDao by lazy { TcaDb.getInstance(this).recentsDao() }
    private val listView by lazy { findViewById(R.id.list) as StickyListHeadersListView }
    private lateinit var adapter: RoomFinderListAdapter

    /**
     * Reconstruct recents from String
     */
    private val recents: List<RoomFinderRoomViewEntity>
        get() {
            return recentsDao.getAll(RecentsDao.ROOMS)?.mapNotNull {
                try {
                    val room = RoomFinderRoom.fromRecent(it)
                    RoomFinderRoomViewEntity.create(room)
                } catch (ignore: IllegalArgumentException) {
                    null
                }
            }.orEmpty()
        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = RoomFinderListAdapter(this, recents)
        listView.setOnItemClickListener(this)

        if (intent != null && intent.hasExtra(SearchManager.QUERY)) {
            requestSearch(intent.getStringExtra(SearchManager.QUERY))
            return
        }

        if (adapter.count == 0) {
            openSearch()
        } else {
            listView.adapter = adapter
        }
    }

    override fun onSearchInBackground() = recents

    override fun onSearchInBackground(query: String): List<RoomFinderRoomViewEntity>? {
        return try {
            TUMCabeClient.getInstance(this)
                    .fetchRooms(userRoomSearchMatching(query))
                    .map { RoomFinderRoomViewEntity.create(it) }
        } catch (e: IOException) {
            Utils.log(e)
            null
        }
    }

    override fun onSearchFinished(searchResult: List<RoomFinderRoomViewEntity>?) {
        if (searchResult == null) {
            if (NetUtils.isConnected(this)) {
                showErrorLayout()
            } else {
                showNoInternetLayout()
            }
            return
        }

        if (searchResult.isEmpty()) {
            listView.adapter = NoResultsAdapter(this)
        } else {
            adapter = RoomFinderListAdapter(this, searchResult)
            listView.adapter = adapter
        }
        showLoadingEnded()
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val room = listView.adapter.getItem(position) as RoomFinderRoom
        openRoomDetails(room)
    }

    /**
     * Opens a [RoomFinderDetailsActivity] that displays details (e.g. location on a map) for
     * a given room. Also adds this room to the recent queries.
     */
    private fun openRoomDetails(room: Serializable) {
        recentsDao.insert(Recent(room.toString(), RecentsDao.ROOMS))

        // Start detail activity
        val intent = Intent(this, RoomFinderDetailsActivity::class.java)
        intent.putExtra(RoomFinderDetailsActivity.EXTRA_ROOM_INFO, room)
        startActivity(intent)
    }

    /**
     * Distinguishes between some room searches, eg. MW 2001 or MI 01.15.069 and takes the
     * number part so that the search can return (somewhat) meaningful results
     * (Temporary and non-optimal)
     *
     * @return a new query or the original one if nothing was matched
     */
    private fun userRoomSearchMatching(roomSearchQuery: String): String {
        // Matches the number part if the String is composed of two words, probably wrong:

        //  First group captures numbers with dots, like the 01.15.069 part from 'MI 01.15.069'
        // (This is the best search format for MI room numbers)
        // The second group captures numbers and mixed formats with letters, like 'MW2001'
        // Only the first match will be returned
        val pattern = Pattern.compile("(\\w+(?:\\.\\w+)+)|(\\w+\\d+)")

        val matcher = pattern.matcher(roomSearchQuery)

        return if (matcher.find()) {
            matcher.group()
        } else {
            roomSearchQuery
        }
    }
}
