package de.tum.`in`.tumcampusapp.component.ui.overview

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import de.tum.`in`.tumcampusapp.api.shared.CacheControl
import de.tum.`in`.tumcampusapp.component.ui.overview.card.Card

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val cardsRepo = CardsRepository(application.applicationContext)

    val cards: LiveData<List<Card>>
        get() = cardsRepo.getCards()

    fun refreshCards() {
        cardsRepo.refreshCards(CacheControl.BYPASS_CACHE)
    }

}