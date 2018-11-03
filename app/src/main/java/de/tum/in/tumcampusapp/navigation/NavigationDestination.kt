package de.tum.`in`.tumcampusapp.navigation

import android.content.Intent
import android.os.Bundle
import de.tum.`in`.tumcampusapp.ui.generic.activity.BaseActivity

sealed class NavigationDestination

data class SystemActivity(
        val clazz: Class<out BaseActivity>,
        val options: Bundle?
) : NavigationDestination()

data class SystemIntent(
        val intent: Intent
) : NavigationDestination()