package de.tum.`in`.tumcampusapp.model.ticket.payload

import com.google.gson.annotations.SerializedName

data class TicketReservationCancelation(@SerializedName("ticket_history")
                                        var ticketHistory: Int = 0)