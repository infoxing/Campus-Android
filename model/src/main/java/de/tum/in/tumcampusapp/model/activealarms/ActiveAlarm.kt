package de.tum.`in`.tumcampusapp.model.activealarms

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings

@Entity(tableName = "active_alarms")
@SuppressWarnings(RoomWarnings.DEFAULT_CONSTRUCTOR)
data class ActiveAlarm(@PrimaryKey var id: Long = 0)
