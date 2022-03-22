package com.example.activityworld.login.network

import com.example.activityworld.login.model.LoggedInUser
import com.example.activityworld.login.model.User

interface LoginInterface {
        suspend fun loginUser(email: String, password: String): LoggedInUser?
}