package com.example.activityworld.booking.network

import android.util.Log
import com.example.activityworld.booking.model.Attributions
import com.example.activityworld.booking.model.Booking
import com.example.activityworld.booking.model.Participant
import com.example.activityworld.network.RequestType
import com.example.activityworld.network.SocketHandler
import java.io.IOException
import java.net.UnknownHostException

class BookingProxy : BookingInterface {
    override suspend fun sendBooking(booking: Booking): Long {
        var bookingID = -1L
        try {
            SocketHandler.apply {
                // Start the connection with the Server
                startConnection()
                // Send the request Type (retrieveFieldsByType)
                sendString(RequestType.INSERT_BOOKING.request)
            }
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {

            // Send booking id
            Log.v("BOOKING_PROXY_SEND_BOOK", "Booking ID = ${booking.id}")
            SocketHandler.sendLong(booking.id)

            // Send booking price
            Log.v("BOOKING_PROXY_SEND_BOOK", "Booking price = ${booking.price}")
            SocketHandler.sendDouble(booking.price)

            // Send field price as Int
            Log.v("BOOKING_PROXY_SEND_BOOK", "Booking date = ${booking.date}")
            SocketHandler.sendLong(booking.date)

            // Send field description
            Log.v("BOOKING_PROXY_SEND_BOOK", "Booking user = ${booking.userId}")
            SocketHandler.sendLong(booking.userId)

            // If the server successfully stored it in the DB, it will return the Booking ID
            bookingID = SocketHandler.readLong()
            if (bookingID != -1L) {
                Log.v("BOOKING_PROXY_SEND_BOOK", "Result: $bookingID")
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        // Close Connection
        SocketHandler.closeConnection()
        Log.v("BOOKING_PROXY_SEND_BOOK", "Booking has been sent")

        return bookingID
    }

    override suspend fun sendParticipants(bookingID: Long, participants: List<Participant>) {
        try {
            SocketHandler.apply {
                // Start the connection with the Server
                startConnection()
                // Send the request Type (retrieveFieldsByType)
                sendString(RequestType.INSERT_PARTICIPANTS.request)
            }
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            // Send the participants list size
            SocketHandler.sendInt(participants.size)
            Log.v("BOOKING_PROXY_SEND_PART", "Number of participants to send: ${participants.size}")

            participants.forEach { participant ->

                // Send Participant id
                Log.v("BOOKING_PROXY_SEND_PART", "Participant ID = ${participant.id}")
                SocketHandler.sendLong(participant.id)

                // Send Participant bookingID
                Log.v("BOOKING_PROXY_SEND_PART", "Participant bookingID = $bookingID")
                SocketHandler.sendLong(bookingID)

                // Send Participant name
                Log.v("BOOKING_PROXY_SEND_PART", "Participant name = ${participant.name}")
                SocketHandler.sendString(participant.name)

                // Send Participant surname
                Log.v("BOOKING_PROXY_SEND_PART", "Participant surname = ${participant.surname}")
                SocketHandler.sendString(participant.surname)


                // Check if the server successfully stored it in db
                val result = SocketHandler.readBoolean()

                Log.v("BOOKING_PROXY_SEND_PART", "Result: $result")
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        // Close Connection
        SocketHandler.closeConnection()
        Log.v("BOOKING_PROXY_SEND_PART", "All participants have been sent")
    }

    override suspend fun sendAttribuzione(attribuzione: Attributions) {
        try {
            SocketHandler.apply {
                // Start the connection with the Server
                startConnection()
                // Send the request Type (retrieveFieldsByType)
                sendString(RequestType.INSERT_LINK.request)
            }
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {

            // Send field id
            Log.v("BOOKING_PROXY_SEND_LINK", "Field ID = ${attribuzione.field}")
            SocketHandler.sendInt(attribuzione.field.toInt())

            // Send booking id
            Log.v("BOOKING_PROXY_SEND_LINK", "Booking ID = ${attribuzione.booking}")
            SocketHandler.sendLong(attribuzione.booking)

            // Send availability id
            Log.v("BOOKING_PROXY_SEND_LINK", "Availability ID = ${attribuzione.availability}")
            SocketHandler.sendLong(attribuzione.availability)

            // Check if the server successfully stored it in db
            val result = SocketHandler.readBoolean()
            Log.v("BOOKING_PROXY_SEND_LINK", "Result: $result")

        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        // Close Connection
        SocketHandler.closeConnection()
        Log.v("BOOKING_PROXY_SEND_LINK", "Link has been sent")
    }

    companion object {
        var instance: BookingProxy? = null
            get() {
                if (field == null) field = BookingProxy()
                return field
            }
            private set
    }
}