package de.tum.`in`.tumcampusapp.ui.cafeteria.repository

import de.tum.`in`.tumcampusapp.api.app.TUMCabeClient
import de.tum.`in`.tumcampusapp.model.cafeteria.Cafeteria
import io.reactivex.Observable

object CafeteriaRemoteRepository {

    lateinit var tumCabeClient: TUMCabeClient

    fun getAllCafeterias(): Observable<List<Cafeteria>> = tumCabeClient.cafeterias

}
