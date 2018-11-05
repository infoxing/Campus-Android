package de.tum.`in`.tumcampusapp.component.ui.eduroam

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.wifi.WifiManager
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.ui.overview.CardAdapter
import de.tum.`in`.tumcampusapp.component.ui.overview.CardManager
import de.tum.`in`.tumcampusapp.component.ui.overview.card.Card
import de.tum.`in`.tumcampusapp.component.ui.overview.card.CardViewHolder
import de.tum.`in`.tumcampusapp.utils.Const
import org.jetbrains.anko.wifiManager

/**
 * Card that can start [SetupEduroamActivity]
 */
class EduroamCard(context: Context) : Card(CardManager.CARD_EDUROAM, context, "card_eduroam") {

    override val optionsMenuResId = R.menu.card_popup_menu

    override fun updateViewHolder(viewHolder: RecyclerView.ViewHolder) {
        val button = viewHolder.itemView.findViewById<MaterialButton>(R.id.eduroam_action_button)
        button.setOnClickListener { v ->
            val intent = Intent(context, SetupEduroamActivity::class.java)
            v.context.startActivity(intent)
        }
    }

    override fun shouldShow(prefs: SharedPreferences): Boolean {
        // Check if WiFi is turned on at all, as we cannot say if it was configured if it is off
        val wifiManager = context.applicationContext.wifiManager
        return wifiManager.isWifiEnabled
                && EduroamController.getEduroamConfig(context) == null
                && eduroamAvailable(wifiManager)
    }

    private fun eduroamAvailable(wifi: WifiManager): Boolean {
        val fineLocation = checkSelfPermission(context, ACCESS_FINE_LOCATION)
        val coarseLocation = checkSelfPermission(context, ACCESS_COARSE_LOCATION)

        if (fineLocation == PERMISSION_GRANTED || coarseLocation == PERMISSION_GRANTED) {
            return wifi.scanResults.any {
                it.SSID == Const.EDUROAM_SSID
            }
        }

        return false
    }

    override fun discard(editor: SharedPreferences.Editor) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean("card_eduroam_start", false)
                .apply()
    }

    override fun getId() = 5000

    companion object : CardAdapter.CardViewHolderFactory {
        override fun inflateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_eduroam, parent, false)
            return CardViewHolder(view)
        }
    }

}
