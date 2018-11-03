package de.tum.`in`.tumcampusapp.component.ui.ticket.viewmodel

import android.content.Context
import android.text.format.DateFormat
import de.tum.`in`.tumcampusapp.component.ui.ticket.model.RawTicket
import org.joda.time.format.DateTimeFormat

data class TicketViewEntity(
        val id: Int,
        val eventId: Int,
        val code: String,
        val ticketTypeId: Int,
        val formattedRedemptionDate: String
) {

    companion object {

        @JvmStatic
        fun create(context: Context, ticket: RawTicket): TicketViewEntity {
            val pattern = if (DateFormat.is24HourFormat(context)) "H:mm" else "h:mm aa"
            val date = DateTimeFormat.shortDate().print(ticket.redemption)
            val time = DateTimeFormat.forPattern(pattern).print(ticket.redemption)
            val formattedTime = "$date, $time"

            return TicketViewEntity(
                    ticket.id, ticket.eventId, ticket.code, ticket.ticketTypeId, formattedTime
            )
        }

    }

}
