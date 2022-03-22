package com.example.activityworld.home.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.activityworld.home.network.HomeProxy
import com.example.activityworld.home.datasource.HomeRepository
import java.lang.IllegalArgumentException

class FieldViewModelFactory(
    private val fieldTypeName: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FieldViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FieldViewModel(
                fieldTypeName = fieldTypeName,
                repository = HomeRepository(homeProxy = HomeProxy.instance)
            ) as T
        } else
            throw IllegalArgumentException("Unknown ViewModel class")
    }
}