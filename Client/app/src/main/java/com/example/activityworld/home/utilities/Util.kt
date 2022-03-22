package com.example.activityworld.home.utilities

import android.annotation.SuppressLint
import android.content.res.Resources
import android.util.Log
import com.example.activityworld.R
import com.example.activityworld.home.constant.Hour
import com.example.activityworld.home.constant.toMilliseconds
import com.example.activityworld.home.model.FieldAvailability
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * These functions create a formatted string that can be set in a TextView.
 */

private val ONE_MINUTE_MILLIS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES)
private val ONE_HOUR_MILLIS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)

/**
 * Convert a duration to a formatted string for display.
 *
 * Examples:
 *
 * 6 seconds on Wednesday
 * 2 minutes on Monday
 * 40 hours on Thursday
 *
 * @param startTimeMilli the start of the interval
 * @param endTimeMilli the end of the interval
 * @param res resources used to load formatted strings
 */
fun convertDurationToFormatted(startTimeMilli: Long, endTimeMilli: Long, res: Resources): String {
    val durationMilli = endTimeMilli - startTimeMilli
    val weekdayString = SimpleDateFormat("EEEE", Locale.getDefault()).format(startTimeMilli)
    return when {
        durationMilli < ONE_MINUTE_MILLIS -> {
            val seconds = TimeUnit.SECONDS.convert(durationMilli, TimeUnit.MILLISECONDS)
            res.getString(R.string.seconds_length, seconds, weekdayString)
        }
        durationMilli < ONE_HOUR_MILLIS -> {
            val minutes = TimeUnit.MINUTES.convert(durationMilli, TimeUnit.MILLISECONDS)
            res.getString(R.string.minutes_length, minutes, weekdayString)
        }
        else -> {
            val hours = TimeUnit.HOURS.convert(durationMilli, TimeUnit.MILLISECONDS)
            res.getString(R.string.hours_length, hours, weekdayString)
        }
    }
}

/*
    /**
     * Returns a string representing the numeric quality rating.
     */
    fun convertNumericQualityToString(quality: Int, resources: Resources): String {
        var qualityString = resources.getString(R.string.three_ok)
        when (quality) {
            -1 -> qualityString = "--"
            0 -> qualityString = resources.getString(R.string.zero_very_bad)
            1 -> qualityString = resources.getString(R.string.one_poor)
            2 -> qualityString = resources.getString(R.string.two_soso)
            4 -> qualityString = resources.getString(R.string.four_pretty_good)
            5 -> qualityString = resources.getString(R.string.five_excellent)
        }
        return qualityString
    }
*/

/**
 * Take the Long milliseconds returned by the system,
 * and convert it to a nicely formatted string for display.
 *
 * EEEE - Display the long letter version of the weekday
 * MMM - Display the letter abbreviation of the month
 * dd-yyyy - day in month and full year numerically
 */
@SuppressLint("SimpleDateFormat")
fun convertLongToDateString(systemTime: Long): String {
    return SimpleDateFormat("EEEE MMM-dd-yyyy")
        .format(systemTime).toString()
}

fun convertLongToDateString2(dateInMillis: Long): String {
    // formatting date in dd-mm-yyyy format.
    val dateFormatter = DateFormat.getDateInstance()
    Log.v("CONVERT_LONG_TO_STRING","DateInMilli is $dateInMillis")

    val dateInString = dateFormatter.format(dateInMillis)
    Log.v("CONVERT_LONG_TO_STRING","DateInString is $dateInString")
    return dateInString
}

fun convertDateStringToLong(dateString: String): Long {
    val dateFormatter = DateFormat.getDateInstance()
    val date = dateFormatter.parse(dateString)
    Log.v("CONVERT_STRING_TO_LONG","DateInString is $dateString")
    Log.v("CONVERT_STRING_TO_LONG","DateInMilli is ${date?.time}")
    return date.time
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}

fun getCurrentDateLong(): Long {
    return getAvailabilitiesDates(1).first()
}

/**
 * Returns a list of date options starting with the current date and the following 3 dates.
 */
 fun getAvailabilitiesDates(numDays: Int): List<Long> {
    val dates = mutableListOf<Long>()
    // Set the formatter for the date
    val dateFormatter = DateFormat.getDateInstance()
    // Get the calendar instance
    val calendar = Calendar.getInstance()
    // Create a list of dates starting with the current date and the following 29 dates
    repeat(numDays) {
        val dateString = dateFormatter.format(calendar.time)
        dates.add(convertDateStringToLong(dateString))
        calendar.add(Calendar.DATE, 1)
    }
    return dates
}

/**
 * Test
 */
fun Date.toString(format: String, locale: Locale = Locale.getDefault()) :String {
    val dateFormatter = DateFormat.getDateInstance()
    val dateString = dateFormatter.format(this)
    Log.v("CONVERT_getDateInstance","DateInString is $dateString")

    return dateString
}

/**
 * Take the Long milliseconds and convert it to an hh:mm format string to display.
 *
 * @param milliSeconds time in milliseconds to convert
 */
fun formatToDigitalClock(milliSeconds: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(milliSeconds).toInt() % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds).toInt() % 60
    //val seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds).toInt() % 60
    return when {
        hours > 0 -> String.format("%d:%02d", hours, minutes)
        minutes > 0 -> String.format("%02d", minutes)
        //seconds > 0 -> String.format("00:%02d", seconds)
        else -> {
            "00:00"
        }
    }
}

fun convertTimeToFormatted(startingTime: Long, endingTime: Long): String {
    // Converts starting and ending time to hh:mm string to display
    val startTime = formatToDigitalClock(startingTime)
    val endTime = formatToDigitalClock(endingTime)

    return "$startTime-$endTime"
}

fun minutesToMilliseconds(minutes: Long): Long {
    return TimeUnit.MINUTES.toMillis(minutes)
}

fun FieldAvailability.isOld(): Boolean {

    val currentTime = System.currentTimeMillis() + Hour.ONE.toMilliseconds()
    Log.v("UTIL_OLD", "Current Time: $currentTime")

    Log.v("UTIL_OLD", "Availability starting time: ${this.date+this.startingTime}")

    Log.v("UTIL_OLD", "Result: ${currentTime<(this.startingTime+this.date)}")

    return currentTime>(this.startingTime+this.date)
}



/*
/**
 * Takes a list of SleepNights and converts and formats it into one string for display.
 *
 * For display in a TextView, we have to supply one string, and styles are per TextView, not
 * applicable per word. So, we build a formatted string using HTML. This is handy, but we will
 * learn a better way of displaying this data in a future lesson.
 *
 * @param   nights - List of all SleepNights in the database.
 * @param   resources - Resources object for all the resources defined for our app.
 *
 * @return  Spanned - An interface for text that has formatting attached to it.
 *           See: https://developer.android.com/reference/android/text/Spanned
 */
fun formatNights(nights: List<SleepNight>, resources: Resources): Spanned {
    val sb = StringBuilder()
    sb.apply {
        append(resources.getString(R.string.title))
        nights.forEach {
            append("<br>")
            append(resources.getString(R.string.start_time))
            append("\t${convertLongToDateString(it.startTimeMilli)}<br>")
            if (it.endTimeMilli != it.startTimeMilli) {
                append(resources.getString(R.string.end_time))
                append("\t${convertLongToDateString(it.endTimeMilli)}<br>")
                append(resources.getString(R.string.quality))
                append("\t${convertNumericQualityToString(it.sleepQuality, resources)}<br>")
                append(resources.getString(R.string.hours_slept))
                // Hours
                append("\t ${it.endTimeMilli.minus(it.startTimeMilli) / 1000 / 60 / 60}:")
                // Minutes
                append("${it.endTimeMilli.minus(it.startTimeMilli) / 1000 / 60}:")
                // Seconds
                append("${it.endTimeMilli.minus(it.startTimeMilli) / 1000}<br><br>")
            }
        }
    }
    // fromHtml is deprecated for target API without a flag, but since our minSDK is 19, we
    // can't use the newer version, which requires minSDK of 24
    //https://developer.android.com/reference/android/text/Html#fromHtml(java.lang.String,%20int)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        return Html.fromHtml(sb.toString())
    }
}

 */
