package com.example.activityworld.booking.model

import com.example.activityworld.home.utilities.getAvailabilitiesDates
import com.example.activityworld.home.utilities.getCurrentDateLong
import com.example.activityworld.home.utilities.getCurrentDateTime
import java.util.*

data class Booking(
    val id: Long = counter++,
    val price: Double,
    val date: Long = getCurrentDateLong(),
    val userId: Long = 1
) {
    companion object {
        var counter: Long = 0L
    }
}
