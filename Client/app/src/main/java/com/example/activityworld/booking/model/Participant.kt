package com.example.activityworld.booking.model

import java.util.*

data class Participant(
    val id: Long = counter++,
    val booking: Booking,
    var name: String = "",
    var surname: String = ""
) {
    companion object {
        var counter: Long = 0L
    }
}
