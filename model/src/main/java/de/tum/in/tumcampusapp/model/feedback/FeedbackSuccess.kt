package de.tum.`in`.tumcampusapp.model.feedback

data class FeedbackSuccess(
        var success: String = "",
        var error: String = "") {

    fun wasSuccessfullySent(): Boolean = error == "" && success != ""
}
