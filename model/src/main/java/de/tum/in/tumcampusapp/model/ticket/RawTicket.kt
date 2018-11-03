package de.tum.`in`.tumcampusapp.model.ticket

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

/**
 * Ticket
 *
 * @param id  ID of ticket_history in DB
 * @param event   Event ID
 * @param code   Ticket Code
 * @param ticketTypeId  ID of TicketType
 * @param redeemed
 */
@Entity(tableName = "tickets")
@SuppressWarnings(RoomWarnings.DEFAULT_CONSTRUCTOR)
data class RawTicket(
        @PrimaryKey
        @SerializedName("ticket_history")
        var id: Int = 0,
        @ColumnInfo(name = "event_id")
        @SerializedName("event")
        var eventId: Int = 0,
        var code: String = "",
        @ColumnInfo(name = "ticket_type_id")
        @SerializedName("ticket_type")
        var ticketTypeId: Int = 0,
        var redemption: DateTime? = null
)
