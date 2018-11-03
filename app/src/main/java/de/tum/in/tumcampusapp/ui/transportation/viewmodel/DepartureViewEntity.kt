package de.tum.`in`.tumcampusapp.ui.transportation.viewmodel

import de.tum.`in`.tumcampusapp.ui.transportation.api.MvvDeparture
import org.joda.time.DateTime
import org.joda.time.Minutes

data class DepartureViewEntity(
        val servingLine: String,
        val direction: String,
        val formattedDirection: String,
        val symbol: String,
        val countDown: Int,
        val departureTime: DateTime
) {

    /**
     * Calculates the countDown with the real departure time and the current time
     *
     * @return The calculated countDown in minutes
     */
    val calculatedCountDown: Int
        get() = Minutes.minutesBetween(DateTime.now(), departureTime).minutes

    companion object {

        fun create(mvvDeparture: MvvDeparture): DepartureViewEntity {
            val formattedDirection = mvvDeparture.servingLine
                    .direction
                    .replace(",", ", ")
                    .replace("\\s+".toRegex(), " ")

            return DepartureViewEntity(
                    mvvDeparture.servingLine.name,
                    mvvDeparture.servingLine.direction,
                    formattedDirection,
                    mvvDeparture.servingLine.symbol,
                    mvvDeparture.countdown,
                    mvvDeparture.dateTime
            )
        }

    }

}
