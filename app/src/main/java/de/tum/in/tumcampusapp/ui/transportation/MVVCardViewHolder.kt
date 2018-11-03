package de.tum.`in`.tumcampusapp.ui.transportation

import android.view.View
import de.tum.`in`.tumcampusapp.ui.overview.card.CardViewHolder
import de.tum.`in`.tumcampusapp.ui.transportation.viewmodel.StationResultViewEntity
import de.tum.`in`.tumcampusapp.ui.transportation.viewmodel.DepartureViewEntity
import kotlinx.android.synthetic.main.card_mvv.view.*

class MVVCardViewHolder(itemView: View) : CardViewHolder(itemView) {

    fun bind(station: StationResultViewEntity, departures: List<DepartureViewEntity>) {
        with(itemView) {
            stationNameTextView.text = station.station

            val controller = TransportController(context)
            val items = Math.min(departures.size, 5)

            if (contentContainerLayout.childCount == 0) {
                departures.asSequence()
                        .take(items)
                        .map { departure ->
                            DepartureView(context).apply {
                                val isFavorite = controller.isFavorite(departure.symbol)
                                setSymbol(departure.symbol, isFavorite)
                                setLine(departure.formattedDirection)
                                setTime(departure.departureTime)
                            }
                        }
                        .toList()
                        .forEach { departureView ->
                            contentContainerLayout.addView(departureView)
                        }
            }
        }
    }

}