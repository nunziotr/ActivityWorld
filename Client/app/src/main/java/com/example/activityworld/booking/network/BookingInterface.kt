package com.example.activityworld.booking.network

import com.example.activityworld.booking.model.Attributions
import com.example.activityworld.booking.model.Booking
import com.example.activityworld.booking.model.Participant

interface BookingInterface {
    suspend fun sendBooking(booking: Booking): Long
    suspend fun sendParticipants(bookingID: Long, participants: List<Participant>)
    suspend fun sendAttribuzione(attribuzione: Attributions)
}