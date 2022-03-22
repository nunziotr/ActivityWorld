package com.example.activityworld.home.datasource

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.activityworld.home.model.FieldAvailability
import com.example.activityworld.home.model.PlayingField
import com.example.activityworld.home.network.HomeProxy


/**
 * HomeRepository provides an interface to fetch fields and availabilityìies.
 *
 * Repository modules handle data operations. They provide a clean API so that the rest of the app
 * can retrieve this data easily. They know where to get the data from and what API calls to make
 * when data is updated. You can consider repositories to be mediators between different data
 * sources, in our case it mediates between a network API and an offline database cache.
 */

class HomeRepository(private val homeProxy: HomeProxy?) {

    private val dataSource = DataSource()

    /**
     * [LiveData] to load playingFields and availabilities.
     *
     * This is the main interface for loading them.
     *
     * Observing this will not cause those items to be refreshed.
     */
    private val _playingFields = MutableLiveData<List<PlayingField>>()
    val playingFields: LiveData<List<PlayingField>> = _playingFields

    // Availabilities filtered by Data&Field
    private val _availabilities = MutableLiveData<List<FieldAvailability>>()
    val availabilities: LiveData<List<FieldAvailability>> = _availabilities

    suspend fun retrieveFieldByType(fieldType: String) {
        try {
            // Make network request using a blocking call
            Log.v("HOME_REPOSITORY", "Requesting fields to the server...")
            _playingFields.postValue(homeProxy?.retrieveFieldByType(fieldType))
            Log.v("HOME_REPOSITORY", "Requesting done")
            Log.v("HOME_REPOSITORY", "Received: ${playingFields.value}")

        } catch (cause: Throwable) {
            // If anything throws an exception, inform the caller
            Log.v("HOME_REPOSITORY", "Entering retrieveFieldByType CATCH")
            throw TitleRefreshError("Unable to retrieve Fields by Type", cause)
        }
    }

    suspend fun sendFields() {

    }

    suspend fun sendAvailabilities() {
        //Log.v("HOME_REPOSITORY", "DataSource, fields size: ${dataSource.fields.size}")
        homeProxy?.sendAvailabilities()
    }

    suspend fun retrieveAvailabilities(field: PlayingField, date: Long) {
        try {
            // Make network request using a blocking call
            Log.v("HOME_REPOSITORY", "Requesting availabilities to the server...")
            _availabilities.postValue(homeProxy?.retrieveAvailabilities(field, date))
            Log.v("HOME_REPOSITORY", "Requesting done")
            Log.v("HOME_REPOSITORY", "Received: ${_availabilities.value}")

        } catch (cause: Throwable) {
            // If anything throws an exception, inform the caller
            Log.v("HOME_REPOSITORY", "Entering retrieveAvailabilities CATCH")
            throw TitleRefreshError("Unable to retrieve Availabilities by Type", cause)
        }
    }

    /**
     * ****************************************************************************************
     * ********************************** FAKE ONES *******************************************
     * ****************************************************************************************
     */
    fun retrieveFieldByTypeFake(fieldType: String) {
        _playingFields.value = dataSource.fields
    }

    fun retrieveAvailabilitiesFake(fieldId: Long, value: Long?) {
       _availabilities.value = dataSource.availabilities
    }
}

/**
 * Thrown when there was a error fetching a new title
 *
 * @property message user ready error message
 * @property cause the original cause of this exception
 */
class TitleRefreshError(message: String, cause: Throwable?) : Throwable(message, cause)

interface TitleRefreshCallback {
    fun onCompleted()
    fun onError(cause: Throwable)
}