package com.example.activityworld.login.network

import android.util.Log
import com.example.activityworld.login.model.LoggedInUser
import com.example.activityworld.network.RequestType
import com.example.activityworld.network.SocketHandler
import java.io.IOException

class LoginProxy : LoginInterface {

    override suspend fun loginUser(email: String, password: String): LoggedInUser? {

        var result = false
        var name: String?
        var id: Long = -1L
        var loggedInUser: LoggedInUser? = null
        try {
            SocketHandler.apply {
                // Start the connection with the Server
                val isConnected = startConnection()
                Log.v("LOGIN_PROXY", "Client was able to connect: $isConnected")
                if(isConnected) {
                    // Send the request Type (Login)
                    sendString(RequestType.LOGIN_USER.request)

                    // Send user's login data
                    sendString(email)
                    sendString(password)

                    // Read the user name if the request was successful
                    id = readLong()
                    name = readString()

                    if(!name.isNullOrEmpty() && id != -1L) {
                        Log.v("SOCKET_HANDLER", "Request successful, User's name: $name")
                        result = true
                        loggedInUser = LoggedInUser(userId = id, displayName = name!!)
                    }

                    // Close the connection
                    closeConnection()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        Log.v("LOGIN_PROXY", "Login result: $result")

        return loggedInUser
    }

    companion object {
        var instance: LoginProxy? = null
            get() {
                if (field == null) field = LoginProxy()
                return field
            }
            private set
    }
}