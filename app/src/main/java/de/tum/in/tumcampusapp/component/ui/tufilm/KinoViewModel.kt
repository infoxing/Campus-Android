package de.tum.`in`.tumcampusapp.component.ui.tufilm

import androidx.lifecycle.ViewModel
import de.tum.`in`.tumcampusapp.component.ui.tufilm.model.RawKino
import de.tum.`in`.tumcampusapp.utils.Utils
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * ViewModel for kinos.
 */
class KinoViewModel(private val localRepository: KinoLocalRepository,
                    private val remoteRepository: KinoRemoteRepository,
                    private val compositeDisposable: CompositeDisposable) : ViewModel() {

    /**
     * Get all kinos from database
     */
    fun getAllKinos(): Flowable<List<RawKino>> =
            KinoLocalRepository.getAllKinos()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .defaultIfEmpty(emptyList())

    /**
     * Get a kino by its position (id)
     */
    fun getKinoByPosition(position: Int): Flowable<RawKino> =
            KinoLocalRepository.getKinoByPosition(position)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    /**
     * Downloads kinos and stores them in the local repository.
     *
     * First checks whether a sync is necessary
     * Then clears current cache
     * Insert new kinos
     * Lastly updates last sync
     *
     */
    fun getKinosFromService(force: Boolean) {
        val latestId = KinoLocalRepository.getLatestId() ?: "0"
        val disposable = remoteRepository
                .getAllKinos(latestId)
                .filter { localRepository.getLastSync() == null || force }
                .doOnNext { localRepository.clear() }
                .doAfterNext { localRepository.updateLastSync() }
                .flatMapIterable { it }
                .filter { it.date.isAfterNow }
                .subscribeOn(Schedulers.io())
                .subscribe({ localRepository.addKino(it) }, { Utils.log(it) })
        compositeDisposable.add(disposable)
    }

    fun getPosition(date: String) = localRepository.getPosition(date)

}