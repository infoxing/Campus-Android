package de.tum.`in`.tumcampusapp.api.shared

enum class CacheControl(val header: String) {
    BYPASS_CACHE("no-cache"),
    USE_CACHE("public")
}