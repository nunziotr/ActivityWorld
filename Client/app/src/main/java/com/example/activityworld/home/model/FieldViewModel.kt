package com.example.activityworld.home.model

import android.util.Log
import androidx.lifecycle.*
import com.example.activityworld.home.constant.FieldType
import com.example.activityworld.home.datasource.DataSource
import com.example.activityworld.home.datasource.HomeRepository
import com.example.activityworld.home.datasource.TitleRefreshError
import com.example.activityworld.home.utilities.convertDateStringToLong
import com.example.activityworld.home.utilities.convertLongToDateString2
import com.example.activityworld.home.utilities.getCurrentDateTime
import com.example.activityworld.home.utilities.toString
import com.example.activityworld.network.ApiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

const val TAG_FIELD_MODEL = "FieldViewModel"


class FieldViewModel(
    private val fieldTypeName: String,
    val repository: HomeRepository
) : ViewModel() {

    private val dataSource = DataSource()

    val fields = repository.playingFields
    private val avails = repository.availabilities

    // Field selected
    private val _field = MutableLiveData<PlayingField?>()
    val field: LiveData<PlayingField?> = _field

    /**
     * Show a loading spinner if true
     */
    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus> = _status

    // ID of the selected availability
    private val _availabilityID = MutableLiveData<Long?>()
    val availabilityID: LiveData<Long?> = _availabilityID

    // Selected availability
    var availability: FieldAvailability? = null

    // Selected Date
    private val _currentSelectedDate = MutableLiveData<Long?>()
    val currentSelectedDate: LiveData<Long?> = _currentSelectedDate

    val dateInString = Transformations.map(_currentSelectedDate) { date ->
        date?.let {
            Log.v(TAG_FIELD_MODEL, "dateInString updated")
            convertLongToDateString2(it)
        }
    }

    val dropMenuItems: Map<String, PlayingField>?
        get() = fields.value?.associateBy { it.name }

    /**
     * ****************************************************************************************
     * ********************************** FAKE ONES *******************************************
     * ****************************************************************************************
     */

    // List of PlayingFields
    private val playingFields = dataSource.fields
    private val availabilities = dataSource.availabilities

    // FieldType selected
    private val _fieldType = MutableLiveData<FieldType?>()
    val fieldType: LiveData<FieldType?> = _fieldType

    // Availabilities filtered by Data&Field
    private val _filteredAvailabilitiesByDate = MutableLiveData<List<FieldAvailability>>()
    val filteredAvailabilitiesByDate: LiveData<List<FieldAvailability>> = _filteredAvailabilitiesByDate

    private var fieldAvailabilities: MutableList<FieldAvailability> = mutableListOf()

    // Filtered Fields by Type
    val filteredFields: List<PlayingField>
        get() = retrieveFields(fieldType)

    val filteredAvailabilitiesByDateFake: List<FieldAvailability>
        get() = retrieveAvailabilitiesByDateFake()

    val dropMenuItemsFake: Map<String, PlayingField>
        get() = filteredFields.associateBy { it.name }


    /**
     * Request a snackbar to display a string.
     *
     * This variable is private because we don't want to expose MutableLiveData
     *
     * MutableLiveData allows anyone to set a value, and MainViewModel is the only
     * class that should be setting values.
     */
    private val _snackBar = MutableLiveData<String?>()

    /**
     * Request a snackbar to display a string.
     */
    val snackbar: LiveData<String?>
        get() = _snackBar



    init {
        //val type = FieldType.valueOf(fieldTypeName)
        //setFieldTypeFake(type)
        retrieveFields()
        setCurrentDate()
    }


    /**
     * Set the field selected.
     */
    fun setField(field: PlayingField) {
        Log.v(TAG_FIELD_MODEL, "setField: $field")
        _field.value = field

        // Update availabilities based on the new selected Field
        retrieveAvailabilities()
        Log.d(TAG_FIELD_MODEL, "Field: $field")
    }


    /**
     * Set selected date.
     */
    fun setDate (date: Long) {
        Log.v(TAG_FIELD_MODEL, "Called setDate")
        _currentSelectedDate.value = date
        Log.v(TAG_FIELD_MODEL, "New Date: ${_currentSelectedDate.value}")

        // Retrieve the field availabilities filtered by Date
        // retrieveAvailabilitiesByDate()
        retrieveAvailabilities()
    }

    /**
     * Set current date
     */
    private fun setCurrentDate() {
        val dateInString = getCurrentDateTime().toString("MMM DD YYYY")
        val dateInMillis = convertDateStringToLong(dateInString)
        Log.v(TAG_FIELD_MODEL, "Called setCurrentDate with date: $dateInString")
        setDate(dateInMillis)
    }

    /**
     * Reset all values pertaining to the FieldType Selected.
     */
    fun resetOrder() {
        _fieldType.value = null
        _field.value = null
        _availabilityID.value = null
        _currentSelectedDate.value = null
        fieldAvailabilities = mutableListOf()
    }

    /**
     * Set the clicked availability
     */
    fun onAvailabilityClicked(id: Long) {
        _availabilityID.value = id
        availability = avails.value?.find { it.id == id }
        Log.v(TAG_FIELD_MODEL, "AvailabilityID sent: ${availabilityID.value}")
        Log.v(TAG_FIELD_MODEL, "Field: ${field.value?.id}")
    }


    /**
     * Retrieve the fields, showing a loading spinner while it refreshes and errors via snackbar.
     */
    private fun retrieveFields() {
        launchDataLoad {
            Log.v(TAG_FIELD_MODEL, "Requesting fields to the server...")
            repository.retrieveFieldByType(fieldTypeName)
            Log.v(TAG_FIELD_MODEL, "Requesting done FIELDS")
            Log.v(TAG_FIELD_MODEL, "Received Fields: ${fields.value}")

            /*
            fields.value?.let { fieldsList ->
                Log.v(TAG_FIELD_MODEL, "Field received, setting the first field: ${fieldsList.first()}")
                setField(fieldsList.first())
            }
            */
        }
    }

    /**
     * Set availabilities filtered on the field selected.
     */
    private fun setAvailabilitiesByField() {
        fieldAvailabilities.clear()
        fieldAvailabilities.addAll(retrieveAvailabilitiesByField())

        // Retrieve the new field availabilities filtered by Date
        retrieveAvailabilities()
    }

    private fun retrieveAvailabilities() {
        launchDataLoad {
            field.value?.let { field ->
                Log.v(TAG_FIELD_MODEL, "Requesting availabilities to the server...")
                currentSelectedDate.value?.let { date ->
                    repository.retrieveAvailabilities(field, date)
                }
            }
            Log.v(TAG_FIELD_MODEL, "Requesting done AVAILABILITIES")
            Log.v(TAG_FIELD_MODEL, "Received Availabilities: ${avails.value}")
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
                _status.postValue(ApiStatus.DONE)
            } catch (error: TitleRefreshError) {
                Log.v(TAG_FIELD_MODEL, "launchDataLoad CATCH")
                _snackBar.postValue(error.message)
                _status.postValue(ApiStatus.ERROR)
                Log.v(TAG_FIELD_MODEL, error.message!!)
            } /*finally {
                _status.value = ApiStatus.DONE
            }*/
        }
    }


    /**
     * ****************************************************************************************
     * ********************************** FAKE ONES *******************************************
     * ****************************************************************************************
     * ****************************************************************************************
     * ********************************** FAKE ONES *******************************************
     * ****************************************************************************************
     * ****************************************************************************************
     * ********************************** FAKE ONES *******************************************
     * ****************************************************************************************
     * ****************************************************************************************
     * ********************************** FAKE ONES *******************************************
     * ****************************************************************************************
     * ****************************************************************************************
     * ********************************** FAKE ONES *******************************************
     * ****************************************************************************************
     * ****************************************************************************************
     * ********************************** FAKE ONES *******************************************
     * ****************************************************************************************
     */




    /**
     * Set the fieldType selected.
     */
    private fun setFieldTypeFake(type: FieldType) {
        Log.v(TAG_FIELD_MODEL, "Setting Type: ${type.name}")
        _fieldType.value = type

        setFieldFake(filteredFields.first())
        setCurrentDateFake()
    }

    /**
     * Set the fieldType selected.
     */
    fun setFieldType(type: FieldType) {
        _fieldType.value = type
        Log.v(TAG_FIELD_MODEL, "fieldType set: ${fieldType.value}")
        retrieveFields()
    }


    /**
     * Set the field selected.
     */
    fun setFieldFake(field: PlayingField) {
        Log.v(TAG_FIELD_MODEL, "setField: $field")
        _field.value = field

        // Update availabilities based on the new selected Field
        setAvailabilitiesByFieldFake()
        Log.d(TAG_FIELD_MODEL, "Field: $field")
    }

    /**
     * Set availabilities filtered on the field selected.
     */
    private fun setAvailabilitiesByFieldFake() {
        fieldAvailabilities.clear()
        fieldAvailabilities.addAll(retrieveAvailabilitiesByField())

        // Retrieve the new field availabilities filtered by Date
        retrieveAvailabilitiesByDate()
    }

    /**
     * Retrieves all the playing fields of type 'fieldType'
     *
     * @param fieldType FieldType used to filter the playing Fields
     */
    private fun retrieveFields(fieldType: LiveData<FieldType?>): List<PlayingField> {
        return playingFields.filter { it.type == fieldType.value }
    }

    /**
     * Retrieves all the [field] availabilities
     *
     */
    private fun retrieveAvailabilitiesByField(): List<FieldAvailability> {
        val newList = availabilities.filter { it.field.id == field.value?.id }
        newList.forEach {
            Log.v(TAG_FIELD_MODEL, "Availability $it.id = $it")
        }
        return newList
    }

    /**
     * Retrieves all the [field] availabilities filtered by the given date
     *
     */
    private fun retrieveAvailabilitiesByDateFake(): List<FieldAvailability> {
        Log.v(TAG_FIELD_MODEL, "Called retrieveAvailabilitiesByDateFake")
        val newList = fieldAvailabilities.filter { it.date == currentSelectedDate.value }
        newList.forEach {
            Log.v(TAG_FIELD_MODEL, "Date = ${currentSelectedDate.value}, Availability ${it.id} = $it")
        }
        return newList
    }

    /**
     * Retrieves all the [field] availabilities filtered by the given date
     * Update filteredAvailabilities MutableLiveData to bind with RecyclerView
     *
     */
    private fun retrieveAvailabilitiesByDate() {
        Log.v(TAG_FIELD_MODEL, "Called retrieveAvailabilitiesByDate")
        _filteredAvailabilitiesByDate.value = fieldAvailabilities.filter { it.date == currentSelectedDate.value }
    }

    /**
     * Set selected date.
     */
    fun setDateFake (date: Long) {
        Log.v(TAG_FIELD_MODEL, "Called setDate")
        _currentSelectedDate.value = date
        Log.v(TAG_FIELD_MODEL, "New Date: ${_currentSelectedDate.value}")

        // Retrieve the field availabilities filtered by Date
        retrieveAvailabilitiesByDate()
    }

    /**
     * Set current date
     */
    private fun setCurrentDateFake() {
        val dateInString = getCurrentDateTime().toString("MMM DD YYYY")
        val dateInMillis = convertDateStringToLong(dateInString)
        Log.v(TAG_FIELD_MODEL, "Called setCurrentDate with date: $dateInString")
        setDateFake(dateInMillis)
    }
}