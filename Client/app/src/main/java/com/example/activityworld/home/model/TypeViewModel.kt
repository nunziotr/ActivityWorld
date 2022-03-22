package com.example.activityworld.home.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.activityworld.home.constant.FieldType
import com.example.activityworld.home.datasource.HomeRepository
import com.example.activityworld.home.network.HomeProxy

class TypeViewModel: ViewModel() {

    val types = FieldType.values().toList()

    // FieldType selected
    private val _fieldType = MutableLiveData<FieldType?>()
    val fieldType: LiveData<FieldType?> = _fieldType

    /**
     * Set the fieldType selected.
     */
    fun setFieldType(type: FieldType) {
        _fieldType.value = type
    }

    fun reset() {
        _fieldType.value = null
    }
}