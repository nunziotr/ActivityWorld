package com.example.activityworld.booking.datasource

import android.util.Log
import com.example.activityworld.booking.model.Attributions
import com.example.activityworld.booking.model.Booking
import com.example.activityworld.booking.model.Participant
import com.example.activityworld.booking.network.BookingProxy
import com.example.activityworld.home.datasource.TitleRefreshError

class BookingRepository(private val bookingProxy: BookingProxy?) {

    suspend fun sendBooking(booking: Booking): Long? {
        var bookingID: Long?
        try {
            bookingID = bookingProxy?.sendBooking(booking)
            Log.v("BOOKING_REPOSITORY", "Booking sent! Booking ID received: $bookingID")
        } catch (cause: Throwable) {
            // If anything throws an exception, inform the caller
            Log.v("HOME_REPOSITORY", "Entering sendBooking CATCH")
            throw TitleRefreshError("Unable to send Booking", cause)
        }
        return bookingID
    }

    suspend fun sendParticipants(bookingID: Long, participants: List<Participant>) {
        try {
            bookingProxy?.sendParticipants(bookingID, participants)
            Log.v("BOOKING_REPOSITORY", "Participants sent!")
        } catch (cause: Throwable) {
            // If anything throws an exception, inform the caller
            Log.v("HOME_REPOSITORY", "Entering sendParticipants CATCH")
            throw TitleRefreshError("Unable to send Participants by Type", cause)
        }
    }

    suspend fun sendAttribution(attribution: Attributions) {
        try {
            bookingProxy?.sendAttribuzione(attribution)
            Log.v("BOOKING_REPOSITORY", "LINK sent!!!")
        } catch (cause: Throwable) {
            // If anything throws an exception, inform the caller
            Log.v("HOME_REPOSITORY", "Entering sendAttribuzione CATCH")
            throw TitleRefreshError("Unable to send LINK by Type", cause)
        }
    }
}