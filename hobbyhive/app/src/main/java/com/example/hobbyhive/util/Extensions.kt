package com.example.hobbyhive.util

object Extensions {
    private val EMAIL_REGEX = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")

    fun String.isValidEmail(): Boolean = EMAIL_REGEX.matches(this.trim())

    fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }

    fun Long.toFormattedDate(): String = DateTimeUtils.formatDate(this)
    fun Long.toFormattedTime(): String = DateTimeUtils.formatTime(this)
    fun Long.toRelativeTime(): String = DateTimeUtils.getRelativeTime(this)
}
