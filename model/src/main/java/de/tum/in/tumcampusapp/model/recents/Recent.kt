package de.tum.`in`.tumcampusapp.model.recents

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings

@Entity
@SuppressWarnings(RoomWarnings.DEFAULT_CONSTRUCTOR)
data class Recent(@PrimaryKey
                  var name: String = "",
                  var type: Int = -1)