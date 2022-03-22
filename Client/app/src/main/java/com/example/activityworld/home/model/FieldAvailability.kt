package com.example.activityworld.home.model

import android.os.Parcelable
import android.util.Log
import com.example.activityworld.home.constant.AvailabilityStatus
import com.example.activityworld.home.utilities.isOld
import kotlinx.parcelize.Parcelize

/**
 * Data class that has information on playing fields availabilities
 */
@Parcelize
data class FieldAvailability(
    val id: Long = counter++,
    val date: Long,
    val startingTime: Long,
    val endingTime: Long,
    val field: PlayingField,
    var isAvailable: Boolean = true,
    var isExpired: Boolean = false,
    //var status: AvailabilityStatus = AvailabilityStatus.AVAILABLE

) : Parcelable {

    val status: AvailabilityStatus
        get() = when {
            isOld() -> {
                Log.v("AVAILABILITY", "EXPIRED")
                AvailabilityStatus.EXPIRED
            }
            isAvailable -> {
                Log.v("AVAILABILITY", "AVAILABLE")
                AvailabilityStatus.AVAILABLE
            }
            else -> {
                Log.v("AVAILABILITY", "RESERVED")
                AvailabilityStatus.RESERVED
            }
        }

    companion object {
        var counter: Long = 0L
    }
}

