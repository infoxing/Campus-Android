package de.tum.`in`.tumcampusapp.model.sync

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import org.joda.time.DateTime

@Entity
@SuppressWarnings(RoomWarnings.DEFAULT_CONSTRUCTOR)
data class Sync(@PrimaryKey
                var id: String = "",
                var lastSync: DateTime = DateTime())