package com.example.activityworld.home.constant

import androidx.annotation.DrawableRes
import com.example.activityworld.R
import com.example.activityworld.home.utilities.minutesToMilliseconds

enum class FieldType(@DrawableRes val drawableRes: Int) {
    SOCCER(R.drawable.calcio_1) {
        override fun selectionAvailabilities(): List<Pair<Long, Long>> {
            return getAvailabilityOptions(Hour.NINE.toMilliseconds(), Hour.TWENTY_TREE.toMilliseconds(), 90)
        }
    },
    TENNIS(R.drawable.tennis_1){
        override fun selectionAvailabilities(): List<Pair<Long, Long>> {
            return getAvailabilityOptions(Hour.NINE.toMilliseconds(), Hour.TWENTY_TREE.toMilliseconds(), 60)
        }
    },
    VOLLEYBALL(R.drawable.pallavolo_2){
        override fun selectionAvailabilities(): List<Pair<Long, Long>> {
            return getAvailabilityOptions(Hour.NINE.toMilliseconds(), Hour.TWENTY_TREE.toMilliseconds(), 60)
        }
    },
    PADDLE(R.drawable.padel_1){
        override fun selectionAvailabilities(): List<Pair<Long, Long>> {
            return getAvailabilityOptions(Hour.NINE.toMilliseconds(), Hour.TWENTY_TREE.toMilliseconds(), 45)
        }
    },
    BASKET(R.drawable.basket_1){
        override fun selectionAvailabilities(): List<Pair<Long, Long>> {
            return getAvailabilityOptions(Hour.NINE.toMilliseconds(), Hour.TWENTY_TREE.toMilliseconds(), 120)
        }
    };

    abstract fun selectionAvailabilities(): List<Pair<Long, Long>>
}

/**
 * Returns a list of time ranges within a day
 * Values are considered to be INT and represent HOURS in MINUTES.
 *
 * @param startingTime Starting range time (Use Hour.HOUR.toMilliseconds())
 * @param endingTime Ending Range time (Use Hour.HOUR.toMilliseconds())
 * @param durationInMinutes Duration of a single match (Add duration in Minutes)
 */
private fun getAvailabilityOptions(startingTime: Long,endingTime: Long, durationInMinutes: Long): List<Pair<Long,Long>> {
    // Converts the received duration from minutes to milliseconds
    // to later perform operation on it
    val duration =  minutesToMilliseconds(durationInMinutes)

    var currentTime = startingTime
    val times = mutableListOf<Pair<Long,Long>>()

    while(currentTime+duration < endingTime) {
        val finishTime = currentTime + duration

        //TODO: Move conversion to BindingAdapters -> setAvailabilityDurationFormatted

        // Converts starting and ending time to hh:mm string to display
        //val startTime = formatToDigitalClock(currentTime)
        //val endTime = formatToDigitalClock(finishTime)

        // Add the newly created time to times
        val time = Pair(currentTime, finishTime)
        times.add(time)

        // Set the new currentTime to the finishTime
        currentTime = finishTime
    }

    return times
}