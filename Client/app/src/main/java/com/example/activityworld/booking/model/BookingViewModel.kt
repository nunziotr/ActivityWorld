package com.example.activityworld.booking.model

import android.util.Log
import androidx.lifecycle.*
import com.example.activityworld.booking.datasource.BookingRepository
import com.example.activityworld.home.datasource.TitleRefreshError
import com.example.activityworld.home.model.FieldAvailability
import com.example.activityworld.home.model.PlayingField
import com.example.activityworld.home.model.TAG_FIELD_MODEL
import com.example.activityworld.home.utilities.convertLongToDateString2
import com.example.activityworld.network.ApiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat

const val TAG_PARTICIPANT_MODEL = "BookingViewModel"

class BookingViewModel(
    private val fieldAvailability: FieldAvailability?,
    val repository: BookingRepository
) : ViewModel() {

    // Booking
    private val _booking = MutableLiveData<Booking?>()
    val booking: LiveData<Booking?> = _booking

    // Availability Selected
    private val _availability = MutableLiveData<FieldAvailability?>()
    val availability: LiveData<FieldAvailability?> = _availability

    // Field Selected
    private val _field = MutableLiveData<PlayingField?>()
    val field: LiveData<PlayingField?> = _field

    // Participants added
    val participants: MutableMap<Long, Participant> = mutableMapOf()

    // Date in String
    val dateInString: String
        get() = availability.value?.let {
            convertLongToDateString2(it.date)
        } ?: ""

    // Total cost of the booking
    private val _total = MutableLiveData(0.0)
    val total: LiveData<String> = Transformations.map(_total) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    /**
     * Show a loading spinner if true
     */
    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus> = _status

    /**
     * Variable that tells the fragment whether it should navigate to [SleepTrackerFragment].
     *
     * This is `private` because we don't want to expose the ability to set [MutableLiveData] to
     * the [Fragment]
     */
    private val _navigateToSleepTracker = MutableLiveData<Boolean?>()

    /**
     * When true immediately navigate back to the [SleepTrackerFragment]
     */
    val navigateToSleepTracker: LiveData<Boolean?>
        get() = _navigateToSleepTracker


    /**
     * Call this immediately after navigating to [SleepTrackerFragment]
     */
    fun doneNavigating() {
        _navigateToSleepTracker.value = null
    }

    fun onClose() {
        _navigateToSleepTracker.value = true
    }




    init {
        onCreate()
    }


    private fun onCreate() {
        setAvailability(fieldAvailability)

        // Create a new Booking
        _total.value?.let {
            _booking.value = Booking(price = it)
        }
    }

    /**
     * Set the availability selected.
     */
    private fun setAvailability(availability: FieldAvailability?) {

        availability?.let {
            Log.v(TAG_PARTICIPANT_MODEL, "availability received: $availability")

            // Set the field and availability
            _availability.value = it
            _field.value = it.field

            // If field is not null, update the Total
            field.value?.let { field ->
                updateTotal(field.price)
            }
        }
    }

    /**
     * Update total value.
     */
    private fun updateTotal(itemPrice: Double) {
        _total.value = itemPrice
    }

    fun addParticipant() {
        val participant = Participant(
            booking = booking.value!!
        )
        participants[participant.id] = participant
    }

    fun removeParticipant(key: Long) {
        participants.remove(key)
    }

    /**
     * Reset all values pertaining to the order.
     */
    fun resetBooking() {
        _booking.value = null
        _availability.value = null
        _field.value = null
        _total.value = 0.0
        participants.clear()
    }

    fun sendBookingAndParticipants() {
        launchDataLoad {
            booking.value?.let { booking ->
                Log.v(TAG_FIELD_MODEL, "Sending the new Booking to the server...")
                val bookingID = repository.sendBooking(booking)
                bookingID?.let {
                    if (participants.isNotEmpty() && bookingID != -1L) {
                        Log.v(TAG_FIELD_MODEL, "Booking sent successfully to the server...")
                        Log.v(TAG_FIELD_MODEL, "Sending the participants to the server...")
                        repository.sendParticipants(bookingID, participants.values.toList())
                    }

                    val link = Attributions(
                        bookingID,
                        availability.value!!.id,
                        field.value!!.id
                    )
                    Log.v(TAG_FIELD_MODEL, "Sending the LINK to the server...")
                    repository.sendAttribution(link)
                }
            }
            Log.v(TAG_FIELD_MODEL, "Booking sent")
        }
    }

    private fun sendParticipants() {
        launchDataLoad {
            if (participants.isNotEmpty()) {
                Log.v(TAG_FIELD_MODEL, "Sending the new Booking to the server...")
                repository.sendParticipants(0, participants.values.toList())
            }
            Log.v(TAG_FIELD_MODEL, "Booking sent")
        }
    }


    /**
     * Helper function to call a data load function with a loading spinner, errors will trigger a
     * snackbar.
     *
     * By marking `block` as `suspend` this creates a suspend lambda which can call suspend
     * functions.
     *
     * @param block lambda to actually load data. It is called in the viewModelScope. Before calling the
     *              lambda the loading spinner will display, after completion or error the loading
     *              spinner will stop
     */
    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            _status.postValue(ApiStatus.LOADING)
            try {
                block()
                Log.v(TAG_FIELD_MODEL, "launchDataLoad after Block()")
                delay(2000)
                _status.postValue(ApiStatus.DONE)
            } catch (error: TitleRefreshError) {
                Log.v(TAG_FIELD_MODEL, "launchDataLoad CATCH")
                //_snackBar.postValue(error.message)
                _status.postValue(ApiStatus.ERROR)
                Log.v(TAG_FIELD_MODEL, error.message!!)
            } /*finally {
                _status.value = ApiStatus.DONE
            }*/
        }
    }
}