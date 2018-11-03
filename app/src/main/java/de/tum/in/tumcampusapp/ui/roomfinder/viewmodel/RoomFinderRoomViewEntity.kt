package de.tum.`in`.tumcampusapp.ui.roomfinder.viewmodel

import de.tum.`in`.tumcampusapp.ui.generic.adapter.SimpleStickyListHeadersAdapter
import de.tum.`in`.tumcampusapp.model.roomfinder.RoomFinderRoom
import java.io.Serializable

data class RoomFinderRoomViewEntity(
        val campus: String,
        val formattedAddress: String,
        val info: String,
        val archId: String,
        val roomId: String,
        val formattedName: String
) : SimpleStickyListHeadersAdapter.SimpleStickyListItem, Serializable {

    override fun getHeadName() = formattedName

    override fun getHeaderId() = headName

    companion object {

        @JvmStatic
        fun create(room: RoomFinderRoom): RoomFinderRoomViewEntity {
            val formattedName = if (room.name == "null") "" else room.name
            val formattedAddress = room.address.trim().replace("(", " (").replace("\\s+".toRegex(), " ")
            return RoomFinderRoomViewEntity(
                    room.campus, formattedAddress, room.info,
                    room.arch_id, room.room_id, formattedName
            )
        }

    }

}
