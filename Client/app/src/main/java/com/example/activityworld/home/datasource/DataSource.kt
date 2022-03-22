package com.example.activityworld.home.datasource

import android.util.Log
import com.example.activityworld.home.constant.FieldType
import com.example.activityworld.home.model.FieldAvailability
import com.example.activityworld.home.model.PlayingField
import com.example.activityworld.home.utilities.getAvailabilitiesDates

/**
 * An object to generate a static list of playing fields
 */
class DataSource {
    val fields: List<PlayingField> = listOf()/*
        PlayingField(
            type = FieldType.SOCCER,
            price = 80.00,
            description = "Soccer playing field, max 10 persons"
        ),
        PlayingField(
            type = FieldType.SOCCER,
            price = 80.00,
            description = "Soccer_2 playing field, max 10 persons",
            name = "Soccer_2"
        ),
        PlayingField(
            type = FieldType.BASKET,
            price = 70.00,
            description = "Basket playing field, max 10 persons",
        ),
        PlayingField(
            type = FieldType.PADDLE,
            price = 40.00,
            description = "Paddle playing field, max 10 persons",
        ),PlayingField(
            type = FieldType.TENNIS,
            price = 20.00,
            description = "Tennis playing field, max 10 persons",
        ),
        PlayingField(
            type = FieldType.VOLLEYBALL,
            price = 30.00,
            description = "VolleyBall playing field, max 10 persons",
        )
    )
    */
    val availabilities: List<FieldAvailability> = listOf() //generateAvailabilities()


    private fun generateAvailabilities(): List<FieldAvailability> {
        val playingFields = fields
        Log.v("DATASOURCE", "Fields size: ${playingFields.size}")
        val availabilities = mutableListOf<FieldAvailability>()

        for (field in playingFields) {
            Log.v("DATASOURCE", "Field: ${field.id}")
            val dates = getAvailabilitiesDates(45)
            val times = field.type.selectionAvailabilities()
            for (date in dates) {
                for (time in times) {
                    val availability = FieldAvailability(
                        date = date,
                        startingTime = time.first,
                        endingTime = time.second,
                        field = field
                    )
                    availabilities.add(availability)
                }
            }
        }
        return availabilities
    }
}

