package de.tum.`in`.tumcampusapp.component.ui.tufilm.viewmodel

import de.tum.`in`.tumcampusapp.model.tufilm.RawKino
import io.reactivex.functions.Function

class KinoViewEntityMapper : Function<List<RawKino>, List<KinoViewEntity>> {

    override fun apply(rawKinos: List<RawKino>): List<KinoViewEntity> {
        return rawKinos.map { KinoViewEntity.create(it) }
    }

}