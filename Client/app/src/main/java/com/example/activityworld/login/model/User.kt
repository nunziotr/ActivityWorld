package com.example.activityworld.login.model

import java.io.Serializable

data class User(
    var email: String,
    var password: String,
    var name: String,
    var surname: String,
    var cf: String,
    var birthdayDate: Long,
    var mobileNumber: String
)
