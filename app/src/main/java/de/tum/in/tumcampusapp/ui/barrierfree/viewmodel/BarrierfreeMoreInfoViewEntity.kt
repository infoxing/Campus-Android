package de.tum.`in`.tumcampusapp.ui.barrierfree.viewmodel

import de.tum.`in`.tumcampusapp.ui.generic.adapter.SimpleStickyListHeadersAdapter
import de.tum.`in`.tumcampusapp.model.barrierfree.BarrierfreeMoreInfo

data class BarrierfreeMoreInfoViewEntity(
        val title: String,
        val category: String,
        val url: String
) : SimpleStickyListHeadersAdapter.SimpleStickyListItem {

    override fun getHeadName() = category

    override fun getHeaderId() = category

    companion object {

        @JvmStatic
        fun create(info: BarrierfreeMoreInfo): BarrierfreeMoreInfoViewEntity {
            return BarrierfreeMoreInfoViewEntity(info.title, info.category, info.url)
        }

    }

}