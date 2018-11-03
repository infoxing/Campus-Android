package de.tum.`in`.tumcampusapp.ui.tufilm

import de.tum.`in`.tumcampusapp.api.app.TUMCabeClient
import de.tum.`in`.tumcampusapp.model.tufilm.RawKino
import io.reactivex.Flowable

object KinoRemoteRepository {

    lateinit var tumCabeClient: TUMCabeClient

    fun getAllKinos(lastId: String): Flowable<List<RawKino>> = tumCabeClient.getKinos(lastId)

}