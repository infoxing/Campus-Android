package de.tum.`in`.tumcampusapp.component.ui.overview

import android.content.Intent
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.generic.activity.BaseActivity
import de.tum.`in`.tumcampusapp.component.other.generic.adapter.EqualSpacingItemDecoration
import de.tum.`in`.tumcampusapp.component.ui.overview.card.Card
import de.tum.`in`.tumcampusapp.component.ui.overview.card.CardViewHolder
import de.tum.`in`.tumcampusapp.service.DownloadService
import de.tum.`in`.tumcampusapp.service.SilenceService
import de.tum.`in`.tumcampusapp.utils.Const
import de.tum.`in`.tumcampusapp.utils.NetUtils
import de.tum.`in`.tumcampusapp.utils.Utils
import org.jetbrains.anko.connectivityManager

/**
 * Main activity displaying the cards and providing navigation with navigation drawer
 */
class MainActivity : BaseActivity(R.layout.activity_main), SwipeRefreshLayout.OnRefreshListener,
        CardInteractionListener {

    private var isConnectivityChangeReceiverRegistered = false

    private val cardsView by lazy { findViewById<RecyclerView>(R.id.cards_view) }
    private val cardAdapter by lazy { CardAdapter() }
    private val swipeRefreshLayout by lazy { findViewById<SwipeRefreshLayout>(R.id.ptr_layout) }
    private val viewModel by lazy {
        ViewModelProviders.of(this)
                .get(MainActivityViewModel::class.java)
    }

    private val networkCallback: NetworkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            runOnUiThread { this@MainActivity.refreshCards() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup pull to refresh
        with(swipeRefreshLayout) {
            setOnRefreshListener(this@MainActivity)
            isRefreshing = true
            setColorSchemeResources(
                    R.color.color_primary,
                    R.color.tum_A100,
                    R.color.tum_A200
            )
        }

        // Setup card RecyclerView
        with(cardsView) {
            registerForContextMenu(this)
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                orientation = RecyclerView.VERTICAL
            }
            setHasFixedSize(true)
            adapter = cardAdapter

            // Add equal spacing between CardViews in the RecyclerView
            val spacing = Math.round(resources.getDimension(R.dimen.material_card_view_padding))
            addItemDecoration(EqualSpacingItemDecoration(spacing))
        }

        showToolbar()

        // Swipe gestures
        ItemTouchHelper(MainActivityTouchHelperCallback()).attachToRecyclerView(cardsView)

        // Start silence Service (if already started it will just invoke a check)
        startService(Intent(this, SilenceService::class.java))

        viewModel.cards.observe(this, Observer { cards ->
            cards?.also { onNewCardsAvailable(it) }
        })
    }

    private fun onNewCardsAvailable(cards: List<Card>) {
        swipeRefreshLayout.isRefreshing = false
        cardAdapter.updateItems(cards)

        if (!NetUtils.isConnected(this) && !isConnectivityChangeReceiverRegistered) {
            val request = NetworkRequest.Builder()
                    .addCapability(NetUtils.internetCapability)
                    .build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
            isConnectivityChangeReceiverRegistered = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun downloadNewsAlert() {
        val downloadService = Intent().apply {
            putExtra(Const.ACTION_EXTRA, Const.DOWNLOAD_ALL_FROM_EXTERNAL)
        }
        DownloadService.enqueueWork(this, downloadService)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isConnectivityChangeReceiverRegistered) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            isConnectivityChangeReceiverRegistered = false
        }
    }

    override fun onResume() {
        super.onResume()

        if (Utils.getSettingBool(this, Const.REFRESH_CARDS, false)) {
            refreshCards()
            Utils.setSetting(this, Const.REFRESH_CARDS, false)
        }
    }

    /**
     * Show progress indicator and start updating cards in background
     */
    fun refreshCards() {
        swipeRefreshLayout.isRefreshing = true
        onRefresh()
        downloadNewsAlert()
    }

    /**
     * Starts updating cards in background
     * Called when [SwipeRefreshLayout] gets triggered.
     */
    override fun onRefresh() {
        viewModel.refreshCards()
    }

    /**
     * Executed when the RestoreCard is pressed
     */
    @Suppress("UNUSED_PARAMETER")
    fun restoreCards(view: View) {
        CardManager.restoreCards(this)
        refreshCards()
    }

    /**
     * Smoothly scrolls the RecyclerView to the top and dispatches nestedScrollingEvents to show
     * the Toolbar
     */
    private fun showToolbar() {
        with(cardsView) {
            startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
            dispatchNestedFling(0f, Integer.MIN_VALUE.toFloat(), true)
            stopNestedScroll()
            layoutManager?.smoothScrollToPosition(cardsView, null, 0)
        }
    }

    override fun onAlwaysHideCard(position: Int) {
        cardAdapter.remove(position)
    }

    /**
     * A touch helper class, Handles swipe to dismiss events
     */
    private inner class MainActivityTouchHelperCallback internal constructor() :
            ItemTouchHelper.SimpleCallback(UP or DOWN, LEFT or RIGHT) {

        override fun getSwipeDirs(recyclerView: RecyclerView,
                                  viewHolder: RecyclerView.ViewHolder): Int {
            val cardViewHolder = viewHolder as CardViewHolder
            val card = cardViewHolder.currentCard
            return if (card == null || !card.isDismissible) {
                0
            } else super.getSwipeDirs(recyclerView, viewHolder)
        }

        override fun onMove(recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder): Boolean {
            cardAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun isLongPressDragEnabled() = true

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val cardViewHolder = viewHolder as CardViewHolder
            val card = cardViewHolder.currentCard ?: return
            val lastPos = cardViewHolder.adapterPosition
            cardAdapter.remove(lastPos)

            val coordinatorLayoutView = findViewById<View>(R.id.coordinator)
            val color = ContextCompat.getColor(baseContext, android.R.color.white)

            Snackbar.make(coordinatorLayoutView, R.string.card_dismissed, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) {
                        cardAdapter.insert(lastPos, card)
                        cardsView.layoutManager
                                ?.smoothScrollToPosition(cardsView, null, lastPos)
                    }.setActionTextColor(color)
                    .addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(snackbar: Snackbar?, event: Int) {
                            super.onDismissed(snackbar, event)
                            if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                                // DISMISS_EVENT_ACTION means, the snackbar was dismissed via the
                                // undo button and therefore, we didn't really dismiss the card
                                card.discardCard()
                            }
                        }
                    }).show()
        }
    }
}
