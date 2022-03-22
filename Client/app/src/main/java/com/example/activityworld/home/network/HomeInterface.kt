package com.example.activityworld.home.network

import com.example.activityworld.home.model.FieldAvailability
import com.example.activityworld.home.model.PlayingField

interface HomeInterface {
    suspend fun retrieveFieldByType(fieldType: String): List<PlayingField>

    // Retrieve availabilities by fields and date
    suspend fun retrieveAvailabilities(field: PlayingField, date: Long): List<FieldAvailability>
    suspend fun sendFields()
    suspend fun sendAvailabilities()
}