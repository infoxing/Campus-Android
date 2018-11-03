package de.tum.`in`.tumcampusapp.ui.barrierfree.viewmodel

import de.tum.`in`.tumcampusapp.ui.generic.adapter.SimpleStickyListHeadersAdapter
import de.tum.`in`.tumcampusapp.model.barrierfree.BarrierfreeContact

data class BarrierfreeContactViewEntity(
        val name: String,
        val telephone: String,
        val email: String,
        val faculty: String,
        val tumID: String,
        val isValid: Boolean,
        val hasTumID: Boolean
) : SimpleStickyListHeadersAdapter.SimpleStickyListItem {

    override fun getHeadName() = faculty

    override fun getHeaderId() = faculty

    companion object {

        @JvmStatic
        fun create(contact: BarrierfreeContact): BarrierfreeContactViewEntity {
            val hasTumID = contact.tumID.isNotBlank() && contact.tumID != "null"
            return BarrierfreeContactViewEntity(
                    contact.name, contact.telephone, contact.email, contact.faculty,
                    contact.tumID, contact.name.isNotBlank(), hasTumID
            )
        }

    }

}
