package com.example.activityworld.booking.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.activityworld.booking.datasource.BookingRepository
import com.example.activityworld.booking.network.BookingProxy
import com.example.activityworld.home.model.FieldAvailability
import java.lang.IllegalArgumentException

class BookingViewModelFactory (
    private val fieldAvailability: FieldAvailability
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookingViewModel(
                fieldAvailability = fieldAvailability,
                repository = BookingRepository(bookingProxy = BookingProxy.instance)
            ) as T
        } else
            throw IllegalArgumentException("Unknown ViewModel class")
    }
}