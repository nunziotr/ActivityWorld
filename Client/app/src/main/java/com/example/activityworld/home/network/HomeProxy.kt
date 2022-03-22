package com.example.activityworld.home.network

import android.util.Log
import com.example.activityworld.home.constant.AvailabilityStatus
import com.example.activityworld.home.constant.FieldType
import com.example.activityworld.home.datasource.DataSource
import com.example.activityworld.home.datasource.DataSource2
import com.example.activityworld.home.model.FieldAvailability
import com.example.activityworld.home.model.PlayingField
import com.example.activityworld.network.RequestType
import com.example.activityworld.network.SocketHandler
import java.io.IOException
import java.net.UnknownHostException

class HomeProxy : HomeInterface {

    override suspend fun retrieveFieldByType(fieldType: String): List<PlayingField> {
        val playingFields: MutableList<PlayingField> = mutableListOf()
        try {
            SocketHandler.apply {
                // Start the connection with the Server
                Log.v("HOME_PROXY_RETRIEVE_F", "Starting request")
                startConnection()
                Log.v("HOME_PROXY_RETRIEVE_F", "Connection Started")

                // Send the request Type (retrieveFieldsByType)
                sendString(RequestType.GET_FIELDS_WITH_TYPE.request)
            }
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            // Send the field type
            SocketHandler.sendString(fieldType)

            // Read PlayingFields list size
            val fieldsNum = SocketHandler.readInt()
            Log.v("HOME_PROXY", "Number of fields to read: $fieldsNum")
            for (elem in 0 until fieldsNum) {

                val id = SocketHandler.readLong()
                Log.v("HOME_PROXY", "Field  n°$elem: ID = $id")
                val price = SocketHandler.readInt().toDouble()
                Log.v("HOME_PROXY", "Field  n°$elem: price = $price")
                val description = SocketHandler.readString()
                Log.v("HOME_PROXY", "Field  n°$elem: description = $description")
                val type = FieldType.valueOf(fieldType)
                Log.v("HOME_PROXY", "Field  n°$elem: type = $type")


                val field = PlayingField(
                    id = id,
                    type = type,
                    price = price,
                    description = description
                )

                Log.v("HOME_PROXY", "Field  n°$elem = $field")

                playingFields.add(field)
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        Log.v("HOME_PROXY", "PlayingFields = $playingFields")

        // Close Connection
        SocketHandler.closeConnection()

        return playingFields
    }

    override suspend fun retrieveAvailabilities(field: PlayingField, date: Long): List<FieldAvailability> {
        val availabilities: MutableList<FieldAvailability> = mutableListOf()
        try {
            SocketHandler.apply {
                // Start the connection with the Server
                Log.v("HOME_PROXY_RETRIEVE_F", "Starting request")
                startConnection()
                Log.v("HOME_PROXY_RETRIEVE_F", "Connection Started")

                // Send the request Type (retrieveFieldsByType)
                sendString(RequestType.GET_AVAILABILITIES_WITH_FIELD_AND_DATE.request)
            }
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            // Send the field ID
            SocketHandler.sendLong(field.id)

            // Send the Date
            SocketHandler.sendLong(date)

            // Read Availabilities list size
            val availabilitiesNum = SocketHandler.readInt()
            Log.v("HOME_PROXY", "Number of Availabilities to read: $availabilitiesNum")
            for (elem in 0 until availabilitiesNum) {

                val id = SocketHandler.readLong()
                Log.v("HOME_PROXY", "Availability  n°$elem: ID = $id")
                val startingTime = SocketHandler.readLong()
                Log.v("HOME_PROXY", "Availability  n°$elem: startingTime = $startingTime")
                val endingTime = SocketHandler.readLong()
                Log.v("HOME_PROXY", "Availability  n°$elem: endingTime = $endingTime")
                val isAvailable = SocketHandler.readBoolean()
                Log.v("HOME_PROXY", "Availability  n°$elem: isAvailable = $isAvailable")

                val availability = FieldAvailability(
                    id = id,
                    date = date,
                    startingTime = startingTime,
                    endingTime = endingTime,
                    field = field,
                    isAvailable = isAvailable
                )

                Log.v("HOME_PROXY", "Availabilities  n°$elem = $field")

                availabilities.add(availability)
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        Log.v("HOME_PROXY", "FieldAvailabilities = $availabilities")

        // Close Connection
        SocketHandler.closeConnection()

        availabilities.sortBy { it.id }
        return availabilities
    }

    override suspend fun sendFields() {
        val dataSource = DataSource()
        val playingFields = dataSource.fields
        try {
            SocketHandler.apply {
                // Start the connection with the Server
                startConnection()
                // Send the request Type (retrieveFieldsByType)
                sendString(RequestType.INSERT_FIELD.request)
            }
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            // Send the playingFields list size
            SocketHandler.sendInt(playingFields.size)
            Log.v("HOME_PROXY_SEND_FIELD", "Number of fields to send: ${playingFields.size}")

            playingFields.forEach { field ->

                // Send field id
                Log.v("HOME_PROXY_SEND_FIELD", "Field ID = ${field.id}")
                SocketHandler.sendLong(field.id)

                // Send field type
                Log.v("HOME_PROXY_SEND_FIELD", "Field type = ${field.type.name}")
                SocketHandler.sendString(field.type.name)

                // Send field price as Int
                Log.v("HOME_PROXY_SEND_FIELD", "Field price = ${field.price.toInt()}")
                SocketHandler.sendInt(field.price.toInt())

                // Send field description
                Log.v("HOME_PROXY_SEND_FIELD", "Field description = ${field.description}")
                SocketHandler.sendString(field.description)

                /*
                // Send field name
                Log.v("HOME_PROXY_SEND_FIELD", "Field  name = ${field.name}")
                SocketHandler.sendString(field.name)
                 */

                // Check if the server successfully stored it in db
                val result = SocketHandler.readBoolean()

                Log.v("HOME_PROXY_SEND_FIELD", "Result: $result")
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        Log.v("HOME_PROXY_SEND_FIELD", "All fields have been sent")

        // Close Connection
        SocketHandler.closeConnection()
    }

    override suspend fun sendAvailabilities() {
        val dataSource = DataSource()
        val availabilities = DataSource2.availabilities
        Log.v("HOME_PROXY", "DataSource, fields size: ${dataSource.fields.size}")

        try {
            SocketHandler.apply {
                // Start the connection with the Server
                startConnection()
                // Send the request Type (retrieveFieldsByType)
                sendString(RequestType.INSERT_AVAILABILITY.request)
            }
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            // Send the availabilities list size
            SocketHandler.sendInt(availabilities.size)
            Log.v("HOME_PROXY_SEND_AV", "Number of availabilities to send: $availabilities.size")

            availabilities.forEach { availability ->

                // Send availability id
                Log.v("HOME_PROXY_SEND_AV", "availability ID = ${availability.id}")
                SocketHandler.sendLong(availability.id)

                // Send availability date
                Log.v("HOME_PROXY_SEND_AV", "availability date = ${availability.date}")
                SocketHandler.sendLong(availability.date)

                // Send availability startingTime
                Log.v("HOME_PROXY_SEND_AV", "availability startingTime = ${availability.startingTime}")
                SocketHandler.sendLong(availability.startingTime)

                // Send availability endingTime
                Log.v("HOME_PROXY_SEND_AV", "availability endingTime = ${availability.endingTime}")
                SocketHandler.sendLong(availability.endingTime)

                // Send availability's Field id
                Log.v("HOME_PROXY_SEND_AV", "availability Field id = ${availability.field.id}")
                SocketHandler.sendLong(availability.field.id)

                // Send availability's status
                Log.v("HOME_PROXY_SEND_AV", "availability status = ${availability.isAvailable}")
                SocketHandler.sendBoolean(availability.isAvailable)

                // Check if the server successfully stored it in db
                val result = SocketHandler.readBoolean()

                Log.v("HOME_PROXY_SEND_AV", "Result: $result")

            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        Log.v("HOME_PROXY_SEND_AV", "All availabilities have been sent")

        // Close Connection
        SocketHandler.closeConnection()
    }

    companion object {
        var instance: HomeProxy? = null
            get() {
                if (field == null) field = HomeProxy()
                return field
            }
            private set
    }
}