package de.tum.`in`.tumcampusapp.component.ui.cafeteria.viewmodel

import de.tum.`in`.tumcampusapp.model.cafeteria.CafeteriaMenu
import io.reactivex.functions.Function

class CafeteriaMenuViewEntityMapper : Function<List<CafeteriaMenu>, List<CafeteriaMenuViewEntity>> {

    override fun apply(menus: List<CafeteriaMenu>): List<CafeteriaMenuViewEntity> {
        return menus.map { CafeteriaMenuViewEntity.create(it) }
    }

}
