package de.tum.`in`.tumcampusapp.ui.cafeteria

import org.joda.time.LocalTime

interface OnNotificationTimeChangedListener {
    fun onTimeChanged(position: Int, newTime: LocalTime)
    fun onCheckChanged(position: Int, isChecked: Boolean)
}