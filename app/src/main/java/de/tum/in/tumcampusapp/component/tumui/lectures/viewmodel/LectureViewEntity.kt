package de.tum.`in`.tumcampusapp.component.tumui.lectures.viewmodel

import de.tum.`in`.tumcampusapp.component.other.generic.adapter.SimpleStickyListHeadersAdapter
import de.tum.`in`.tumcampusapp.model.lecture.Lecture

data class LectureViewEntity(
        val duration: String,
        val chairTumId: String?,
        val chairName: String?,
        val chairId: Int?,
        val semester: String,
        val semesterId: String,
        val semesterName: String,
        val semesterYears: String,
        val shortLectureType: String,
        val lectureType: String,
        val lectureId: String,
        val stp_sp_nr: String,
        val stp_sp_sst: String,
        val title: String,
        val lecturers: String?
) : Comparable<Lecture>, SimpleStickyListHeadersAdapter.SimpleStickyListItem {

    override fun compareTo(other: Lecture) = other.semesterId.compareTo(semesterId)

    override fun getHeadName() = semesterName

    override fun getHeaderId() = semesterId

    companion object {

        @JvmStatic
        fun create(lecture: Lecture): LectureViewEntity {
            return LectureViewEntity(
                    lecture.duration, lecture.chairTumId, lecture.chairName, lecture.chairId,
                    lecture.semester, lecture.semesterId, lecture.semesterName, lecture.semesterYears,
                    lecture.shortLectureType, lecture.lectureType, lecture.lectureId, lecture.stp_sp_nr,
                    lecture.stp_sp_sst, lecture.title, lecture.lecturers
            )
        }

    }

}
