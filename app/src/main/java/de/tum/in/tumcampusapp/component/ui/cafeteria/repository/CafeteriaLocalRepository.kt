package de.tum.`in`.tumcampusapp.component.ui.cafeteria.repository

import de.tum.`in`.tumcampusapp.component.ui.cafeteria.controller.CafeteriaManager
import de.tum.`in`.tumcampusapp.model.cafeteria.Cafeteria
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.viewmodel.CafeteriaWithMenus
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.viewmodel.CafeteriaMenuViewEntity
import de.tum.`in`.tumcampusapp.database.TcaDb
import de.tum.`in`.tumcampusapp.utils.sync.model.Sync
import io.reactivex.Flowable
import org.joda.time.DateTime
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object CafeteriaLocalRepository {

    private const val TIME_TO_SYNC = 604800

    private val executor: Executor = Executors.newSingleThreadExecutor()

    lateinit var db: TcaDb

    fun getCafeteriaWithMenus(cafeteriaId: Int): CafeteriaWithMenus {
        return CafeteriaWithMenus(cafeteriaId).apply {
            name = getCafeteriaNameFromId(id)
            menuDates = getAllMenuDates()
            menus = getCafeteriaMenus(id, nextMenuDate)
        }
    }

    private fun getCafeteriaNameFromId(id: Int): String? = getCafeteria(id)?.name

    // Menu methods //

    fun getCafeteriaMenus(id: Int, date: DateTime): List<CafeteriaMenuViewEntity> {
        return db.cafeteriaMenuDao()
                .getTypeNameFromDbCard(id, date)
                .map { CafeteriaMenuViewEntity.create(it) }
    }

    fun getAllMenuDates(): List<DateTime> = db.cafeteriaMenuDao().allDates

    // Canteen methods //

    fun getAllCafeterias(): Flowable<List<Cafeteria>> = db.cafeteriaDao().all

    fun getCafeteria(id: Int): Cafeteria? = db.cafeteriaDao().getById(id)

    fun addCafeteria(cafeteria: Cafeteria) = executor.execute { db.cafeteriaDao().insert(cafeteria) }

    // Sync methods //

    fun getLastSync() = db.syncDao().getSyncSince(CafeteriaManager::class.java.name, TIME_TO_SYNC)

    fun updateLastSync() = db.syncDao().insert(Sync(CafeteriaManager::class.java.name, DateTime.now()))

    fun clear() = db.cafeteriaDao().removeCache()

}


