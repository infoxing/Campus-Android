package de.tum.`in`.tumcampusapp.component.ui.tufilm

import de.tum.`in`.tumcampusapp.model.tufilm.RawKino
import de.tum.`in`.tumcampusapp.database.TcaDb
import de.tum.`in`.tumcampusapp.model.sync.Sync
import io.reactivex.Flowable
import org.joda.time.DateTime

object KinoLocalRepository {

    private const val TIME_TO_SYNC = 1800

    lateinit var db: TcaDb

    fun getLastSync() = db.syncDao().getSyncSince(RawKino::class.java.name, TIME_TO_SYNC)

    fun updateLastSync() = db.syncDao().insert(Sync(RawKino::class.java.name, DateTime.now()))

    fun addKino(kino: RawKino) = db.kinoDao().insert(kino)

    fun getAllKinos(): Flowable<List<RawKino>> = db.kinoDao().all

    fun getLatestId(): String? = db.kinoDao().latestId

    fun getKinoByPosition(position: Int): Flowable<RawKino> = db.kinoDao().getByPosition(position)

    fun clear() = db.kinoDao().cleanUp()

    fun getPosition(date: String) = db.kinoDao().getPosition(date)

}