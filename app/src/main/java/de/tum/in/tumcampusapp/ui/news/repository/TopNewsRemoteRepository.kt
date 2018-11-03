package de.tum.`in`.tumcampusapp.ui.news.repository

import de.tum.`in`.tumcampusapp.api.app.TUMCabeClient
import de.tum.`in`.tumcampusapp.model.news.NewsAlert
import io.reactivex.Observable

object TopNewsRemoteRepository {

    lateinit var tumCabeClient: TUMCabeClient

    fun getNewsAlert(): Observable<NewsAlert> = tumCabeClient.newsAlert

}